package payon.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import payon.security.PayonSecurity;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PayonHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final long mcId;
    private final String appId;
    private final String secretKey;
    private final String url;
    private final String httpAuth;
    private final String httpAuthPass;
    private final String refCode;
    private boolean sslVerifypeer;
    private int maxIdleConnections;
    private long keepAliveDurationMS;
    private long connectTimeout;

    public PayonHelper(long mcId, String appId, String secretKey, String url, String httpAuth, String httpAuthPass) {
        this(mcId, appId, secretKey, url, httpAuth, httpAuthPass, 0, 300000L, 30000L);
    }

    public PayonHelper(long mcId,
                       String appId,
                       String secretKey,
                       String url,
                       String httpAuth,
                       String httpAuthPass,
                       int maxIdleConnections,
                       long keepAliveDurationMS,
                       long connectTimeout) {
        this.mcId = mcId;
        this.appId = appId;
        this.secretKey = secretKey;
        this.url = url;
        this.httpAuth = httpAuth;
        this.httpAuthPass = httpAuthPass;
        this.sslVerifypeer = true;
        this.refCode = "MCAPI-JV";
        this.setMaxIdleConnections(maxIdleConnections);
        this.setKeepAliveDurationMS(keepAliveDurationMS);
        this.setConnectTimeout(connectTimeout);
    }

    public boolean isSslVerifypeer() {
        return sslVerifypeer;
    }

    public void setSslVerifypeer(boolean sslVerifypeer) {
        this.sslVerifypeer = sslVerifypeer;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        if(maxIdleConnections < 0) {
            throw new IllegalArgumentException("Max idle connections < 0: " + maxIdleConnections);
        }
        this.maxIdleConnections = maxIdleConnections;
    }

    public long getKeepAliveDurationMS() {
        return keepAliveDurationMS;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        if (connectTimeout < 0L) {
            throw new IllegalArgumentException("timeout < 0");
        }
        this.connectTimeout = connectTimeout;
    }

    public void setKeepAliveDurationMS(long keepAliveDurationMS) {
        if (keepAliveDurationMS <= 0L) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDurationMS);
        }
        this.keepAliveDurationMS = keepAliveDurationMS;
    }

    /**
     *
     * @param mapParams - Map of payment information
     * @return PayonResponse
     * @throws Exception - Exception throws when call api get error
     */
    public PayonResponse createOrderPaynow(Map<String, Object> mapParams) throws Exception {
        mapParams.put("merchant_id", this.mcId);
        return this.buildPayment("createOrderPaynow", mapParams);
    }

    /**
     *
     * @param merchantRequestId - merchant request id when order was created
     * @return PayonResponse
     * @throws Exception - Exception throws when call api get error
     */
    public PayonResponse checkPayment(String merchantRequestId) throws Exception {
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("merchant_request_id", merchantRequestId);
        return buildPayment("checkPayment", mapParams);

    }

    /**
     *
     * @return PayonResponse
     * @throws Exception - Exception throws when call api get error
     */
    public PayonResponse getBankInstallment() throws Exception {
        Map<String, Object> mapParams = new HashMap<>();
        return this.buildPayment("getBankInstallmentV2", mapParams);
    }

    /**
     *
     * @param mapParams - Map of payment information
     * @return PayonResponse
     * @throws Exception - Exception throws when call api get error
     */
    public PayonResponse getFee(Map<String, Object> mapParams) throws Exception {
        mapParams.put("merchant_id", this.mcId);
        return this.buildPayment("getFeeInstallmentv2", mapParams);
    }

    /**
     *
     * @param mapParams - Map of payment information
     * @return PayonResponse
     * @throws Exception - Exception throws when call api get error
     */
    public PayonResponse createOrderInstallment(Map<String, Object> mapParams) throws Exception {
        mapParams.put("merchant_id", this.mcId);
        return this.buildPayment("createOrderInstallment", mapParams);
    }

    /**
     *
     * @return PayonResponse
     * @throws Exception - Exception throws when call api get error
     */
    public PayonResponse getQrBankCode() throws Exception {
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("service_type_code", "PAYNOW");
        mapParams.put("service_code", "PAYNOW_QRLOCALBANK_DYNAMIC");
        mapParams.put("method_code", "LOCALBANK");
        return buildPayment("getQrBankCode", mapParams);
    }

    /**
     *
     * @param mapParams - Map of payment information
     * @return PayonResponse
     * @throws Exception - Exception throws when call api get error
     */
    public PayonResponse createQrCode(Map<String, Object> mapParams) throws Exception {
        mapParams.put("merchant_id", this.mcId);
        mapParams.put("service_type_code", "PAYNOW");
        mapParams.put("service_code", "PAYNOW_QRLOCALBANK_DYNAMIC");
        mapParams.put("method_code", "LOCALBANK");
        mapParams.put("currency", "VND");
        return this.buildPayment("createQRCode", mapParams);
    }

    /**
     *
     * @param data - provide data from request
     * @param checksum - provide checksum from request
     * @return boolean
     */
    public boolean validateNotify(String data, String checksum) {
        String dataChecksum = PayonSecurity.md5(this.appId + data + this.secretKey);
        return checksum.equals(dataChecksum);
    }

    private PayonResponse buildPayment(String uri,Object params) throws Exception {
        String data = encryptDate(params, this.secretKey);
        String checksum = PayonSecurity.md5(new StringBuilder()
                .append(this.appId)
                .append(data)
                .append(this.secretKey).toString());

        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("app_id", this.appId);
        mapParams.put("data", data);
        mapParams.put("checksum", checksum);
        mapParams.put("ref_code", this.refCode);
        String requestBody = objectMapper.writeValueAsString(mapParams);

        return call(uri, this.httpAuth, this.httpAuthPass, requestBody);
    }

    private PayonResponse call(String uri,
                        String username,
                        String password,
                        String requestBody) throws Exception {

        //Call URL
        String callUrl = this.url;
        String lastChar = callUrl.substring(callUrl.length()-1);
        uri = uri.replace("/","");
        if(!lastChar.equals("/")){
            callUrl = callUrl + "/";
        }
        callUrl += uri;

        // prepare data for request header
        String authorization = Base64.getEncoder().encodeToString(
                new StringBuilder()
                        .append(username)
                        .append(":")
                        .append(password)
                        .toString().getBytes());

        OkHttpClient httpClient = new OkHttpClient();

        if(connectTimeout > 0L) {
            httpClient.setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }

        if(maxIdleConnections > 0L) {
            httpClient.setConnectionPool(new ConnectionPool(maxIdleConnections, keepAliveDurationMS));
        }

        if(this.sslVerifypeer == false) {
            final TrustManager[] trustAllCerts = trustAllCerts();
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create a ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            httpClient.setSslSocketFactory(sslSocketFactory);
            httpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }


        MediaType mediaType = MediaType.parse("application/json; utf-8");
        RequestBody body = RequestBody.create(mediaType, requestBody);
        Request request = new Request.Builder()
                .url(callUrl)
                .method("POST", body)
                .addHeader("Content-Type", "application/json; utf-8")
                .addHeader("Authorization", "Basic "+ authorization)
                .build();
        Response response = httpClient.newCall(request).execute();

        String bodyResp = response.body().string();
        PayonResponse rs = objectMapper.readValue(bodyResp, PayonResponse.class);
        rs.setHttpStatus(response.code());
        System.out.println("RESPONSE: "+ bodyResp);
        return rs;
    }



    private String encryptDate(Object obj, String mcSecretKey) throws Exception {
        PayonSecurity security = new PayonSecurity(mcSecretKey);
        String data = objectMapper.writeValueAsString(obj);
        return security.encrypt(data);
    }

    private TrustManager[] trustAllCerts() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
    }
}

