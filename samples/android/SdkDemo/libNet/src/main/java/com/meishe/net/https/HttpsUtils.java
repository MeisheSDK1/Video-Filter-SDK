/*
 * Copyright 2016 jeasonlzy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meishe.net.https;

import com.meishe.net.utils.OkLogger;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：Https相关的工具类
 * Https related utility classes
 * 修订历史：
 * ================================================
 */
public class HttpsUtils {

    /**
     * The type Ssl params.
     * Ssl参数
     */
    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        /**
         * The Trust manager.
         * 信任管理器
         */
        public X509TrustManager trustManager;
    }

    /**
     * Gets ssl socket factory.
     * 获取ssl套接字工厂
     * @return the ssl socket factory
     */
    public static SSLParams getSslSocketFactory() {
        return getSslSocketFactoryBase(null, null, null);
    }

    /**
     * https单向认证
     * 可以额外配置信任服务端的证书策略，否则默认是按CA证书去验证的，若不是CA可信任的证书，则无法通过验证
     * HTTPS one-way authentication
     * * Additional configuration of the trust server's certificate policy is possible; otherwise, the default is to authenticate with a CA certificate. If it is not a CA trusted certificate, it cannot be authenticated
     * @param trustManager the trust manager
     * @return the ssl socket factory
     */
    public static SSLParams getSslSocketFactory(X509TrustManager trustManager) {
        return getSslSocketFactoryBase(trustManager, null, null);
    }

    /**
     * https单向认证
     * 用含有服务端公钥的证书校验服务端证书
     * HTTPS one-way authentication
     * * Additional configuration of the trust server's certificate policy is possible; otherwise, the default is to authenticate with a CA certificate. If it is not a CA trusted certificate, it cannot be authenticated
     * @param certificates the certificates 证书
     * @return the ssl socket factory
     */
    public static SSLParams getSslSocketFactory(InputStream... certificates) {
        return getSslSocketFactoryBase(null, null, null, certificates);
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * certificates -> 用含有服务端公钥的证书校验服务端证书
     * HTTPS bidirectional authentication
     * * bksFile and password-> clients use BKS certificates to verify server certificates
     * * certificates -> verifies the server's certificate with a certificate containing the server's public key
     * @param bksFile      the bks file 募集文件
     * @param password     the password 密码
     * @param certificates the certificates 证书
     * @return the ssl socket factory
     */
    public static SSLParams getSslSocketFactory(InputStream bksFile, String password, InputStream... certificates) {
        return getSslSocketFactoryBase(null, bksFile, password, certificates);
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * X509TrustManager -> 如果需要自己校验，那么可以自己实现相关校验，如果不需要自己校验，那么传null即可
     * HTTPS bidirectional authentication
     * * bksFile and password-> clients use BKS certificates to verify server certificates
     * * X509TrustManager -> if you need to verify yourself, you can implement relevant validation by yourself, if you do not need to verify yourself, then pass null
     * @param bksFile      the bks file 募集文件
     * @param password     the password 密码
     * @param trustManager the trust manager 信任管理
     * @return the ssl socket factory
     */
    public static SSLParams getSslSocketFactory(InputStream bksFile, String password, X509TrustManager trustManager) {
        return getSslSocketFactoryBase(trustManager, bksFile, password);
    }

    private static SSLParams getSslSocketFactoryBase(X509TrustManager trustManager, InputStream bksFile, String password, InputStream... certificates) {
        SSLParams sslParams = new SSLParams();
        try {
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            X509TrustManager manager;
            if (trustManager != null) {
                //优先使用用户自定义的TrustManager User - defined TrustManager is preferred
                manager = trustManager;
            } else if (trustManagers != null) {
                //然后使用默认的TrustManager Then use the default TrustManager
                manager = chooseTrustManager(trustManagers);
            } else {
                //否则使用不安全的TrustManager Otherwise, use an insecure TrustManager
                manager = UnSafeTrustManager;
            }
            // 创建TLS类型的SSLContext对象， that uses our TrustManager Create an SSLContext object of type TLS, that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书 Initialize the SSLContext with the trustManagers obtained above so that the SSLContext trusts the certificate in the keyStore
            // 第一个参数是授权的密钥管理器，用来授权验证，比如授权自签名的证书验证。第二个是被授权的证书管理器，用来验证服务器端的证书 The first parameter is the authorized key manager, which is used to authorize authentication, such as authentication of the authorized self-signed certificate. The second is the authorized certificate manager, which validates the server-side certificate
            sslContext.init(keyManagers, new TrustManager[]{manager}, null);
            // 通过sslContext获取SSLSocketFactory对象 Get the SSLSocketFactory object through sslContext
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = manager;
            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKeyStore, password.toCharArray());
            return kmf.getKeyManagers();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
        return null;
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            // 创建一个默认类型的KeyStore，存储我们信任的证书 Create a KeyStore of the default type that stores the certificates we trust
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certStream : certificates) {
                String certificateAlias = Integer.toString(index++);
                // 证书工厂根据证书文件的流生成证书 cert The certificate factory generates the Certificate CERT based on the flow of the certificate file
                Certificate cert = certificateFactory.generateCertificate(certStream);
                // 将 cert 作为可信证书放入到keyStore中 Place CERT as a trust certificate in the keyStore
                keyStore.setCertificateEntry(certificateAlias, cert);
                try {
                    if (certStream != null) certStream.close();
                } catch (IOException e) {
                    OkLogger.printStackTrace(e);
                }
            }
            //我们创建一个默认类型的TrustManagerFactory We create a TrustManagerFactory of the default type
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            //用我们之前的keyStore实例初始化TrustManagerFactory，这样tmf就会信任keyStore中的证书 Initialize the TrustManagerFactory with our previous keyStore instance so that TMF trusts the certificate in the keyStore
            tmf.init(keyStore);
            //通过tmf获取TrustManager数组，TrustManager也会信任keyStore中的证书 Get the TrustManager array through TMF, and the TrustManager will also trust the certificates in the keyStore
            return tmf.getTrustManagers();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    /**
     * 为了解决客户端不信任服务器数字证书的问题，网络上大部分的解决方案都是让客户端不对证书做任何检查，
     * 这是一种有很大安全漏洞的办法
     * To address the client's distrust of the server's digital certificate, most of the solutions on the network are to have the client do no checking of the certificate,
     * * This is a method with a big security hole
     */
    public static X509TrustManager UnSafeTrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    };

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     * This class is the base interface for hostname validation. During the handshake, if the hostname of the URL does not match the identifying hostname of the server,
     * * The validation mechanism can call back the implementer of this interface to determine whether the connection should be allowed. Policies can be certificate-based or depend on other authentication schemes.
     * * Use these callbacks when the default rules used to validate URL host names fail. Returns true if the host name is acceptable
     */
    public static HostnameVerifier UnSafeHostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
