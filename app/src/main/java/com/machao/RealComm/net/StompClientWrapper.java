package com.machao.RealComm.net;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.machao.RealComm.contract.presenter.WebrtcPresenter;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okio.Buffer;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class StompClientWrapper {
    private static final String TAG = WebrtcPresenter.class.getName();

    private static final String URI = "wss://47.75.12.61:8443/RealComm/websocket";
//    private static final String URI = "wss://172.16.4.135:8443/RealComm/websocket";
    private static final String SERVER_CRT =
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIICjTCCAfYCCQCALCcme/0H/jANBgkqhkiG9w0BAQsFADCBijELMAkGA1UEBhMC\n" +
                    "Q04xETAPBgNVBAgMCExpYW9OaW5nMQ8wDQYDVQQHDAZEYWxpYW4xEjAQBgNVBAoM\n" +
                    "CUdhbmppbmd6aTEQMA4GA1UECwwHcHJpdmF0ZTEPMA0GA1UEAwwGbWFjaGFvMSAw\n" +
                    "HgYJKoZIhvcNAQkBFhFaVUltYWNoYW9AMTYzLmNvbTAeFw0xODAyMDgxMjIyMDZa\n" +
                    "Fw0xOTAyMDgxMjIyMDZaMIGKMQswCQYDVQQGEwJDTjERMA8GA1UECAwITGlhb05p\n" +
                    "bmcxDzANBgNVBAcMBkRhbGlhbjESMBAGA1UECgwJR2FuamluZ3ppMRAwDgYDVQQL\n" +
                    "DAdwcml2YXRlMQ8wDQYDVQQDDAZtYWNoYW8xIDAeBgkqhkiG9w0BCQEWEVpVSW1h\n" +
                    "Y2hhb0AxNjMuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFIFoFB3yX\n" +
                    "x8qekhCRfGCFThEwzpmNBJMWG9Uw59cRnHrwSvQR6gjHg8aE1oR/ThYLGS+REDtE\n" +
                    "xiJL3mXEJOmCO8zb26K5nXkgAIzSyrT1AHrOWu7uxaGizzJj787VYszb0pJ3uaVZ\n" +
                    "SLIFXM7hGjIjeoSEe6ucl6kF7kCkKwp6OwIDAQABMA0GCSqGSIb3DQEBCwUAA4GB\n" +
                    "ADier2KhWFiCmEIrdyjD3UOPupmX6B2diSc+VRkbY17LcdmtgCGxCNgk6Gx13xbF\n" +
                    "DLuo03wPAQr4GDPrpHBdQUFQspL9/GrkePgM+AQtKSNOS4B00FITrbcw6j22fh7D\n" +
                    "XRQJzzDByEycZ9LgstulHf3IfR22FWQiW67/y6EcxnSa\n" +
                    "-----END CERTIFICATE-----\n";

    private final StompClient stompClient;
    private StompLifecycleListener stompLifecycleListener;

    public interface StompLifecycleListener {
        void onOpen();
        void onClose();
        void onError();
    }

    public StompClientWrapper() throws CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        keyStore.setCertificateEntry("SERVER_CRT", certificateFactory.generateCertificate(new Buffer().writeUtf8(SERVER_CRT).inputStream()));

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient httpClient = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }).build();

        Map<String, String> header = new HashMap<>();
        this.stompClient = Stomp.over(WebSocket.class, URI, header, httpClient);
        this.stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "Stomp connection opened");
                    if(stompLifecycleListener != null)  stompLifecycleListener.onOpen();
                    break;
                case ERROR:
                    Log.e(TAG, "Error", lifecycleEvent.getException());
                    if(stompLifecycleListener != null)  stompLifecycleListener.onError();
                    break;
                case CLOSED:
                    Log.d(TAG, "Stomp connection closed");
                    if(stompLifecycleListener != null)  stompLifecycleListener.onClose();
                    break;
            }
        });
    }

    public void connect() {
        this.stompClient.connect();
    }

    public void connect(boolean reconnect) {
        this.stompClient.connect(reconnect);
    }

    public void connect(List<StompHeader> headers) {
        this.stompClient.connect(headers);
    }

    public void connect(List<StompHeader> headers, boolean reconnect) {
        this.stompClient.connect(headers, reconnect);
    }

    public Flowable<Void> send(String destination) {
        Log.d(TAG, "send message: " + destination);
        return stompClient.send(destination);
    }

    public Flowable<Void> send(String destination, String data) {
        Log.d(TAG, "send message: " + destination + ": " + data);
        return stompClient.send(destination, data);
    }

    public Flowable<Void> send(String destination, Object data) {
        Log.d(TAG, "send message: " + destination + ": " + JSONObject.toJSONString(data));
        return stompClient.send(destination, JSONObject.toJSONString(data));
    }

    public Flowable<Void> send(StompMessage stompMessage) {
        return stompClient.send(stompMessage);
    }

    public Flowable<LifecycleEvent> lifecycle() {
        return stompClient.lifecycle();
    }

    public void disconnect() {
        this.stompClient.disconnect();
    }

    public Flowable<StompMessage> topic(String destinationPath) {
        return stompClient.topic(destinationPath);
    }

    public Flowable<StompMessage> topic(String destinationPath, List<StompHeader> headerList) {
        return stompClient.topic(destinationPath, headerList);
    }

    public boolean isConnected() {
        return stompClient.isConnected();
    }

    public boolean isConnecting() {
        return stompClient.isConnecting();
    }

    public void setLifecycleListener(StompLifecycleListener stompLifecycleListener) {
        this.stompLifecycleListener = stompLifecycleListener;
    }
}
