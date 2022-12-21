# Payon Java SDK

# Giới thiệu thông tin

- Package hỗ trợ thưc thi các API theo tài liệu: [https://docs.nextpay.vn](https://docs.nextpay.vn/)
    - Thanh toán ngay
    - Lấy danh sách ngân hàng hỗ trợ thanh toán bằng QR-Code
    - Thanh toán bằng QR-Code
    - Lấy danh sách ngân hàng hỗ trợ trả góp
    - Thông tin phí trả góp
    - Tạo yêu cầu thanh toán trả góp
    - Kiểm tra giao dịch
    - Hỗ trợ connect pool

# Cài đặt và loading

- Cài dặt bằng Maven
```xml
<dependency>
    <groupId>io.github.nextpaygroup</groupId>
    <artifactId>payon-java-sdk</artifactId>
    <version>1.0.4</version>
</dependency>
```
- java 8
```xml
<dependency>
    <groupId>io.github.nextpaygroup</groupId>
    <artifactId>payon-java-sdk</artifactId>
    <version>1.0.4.8</version>
</dependency>
```
- Cài đặt bằng Gradle
```
implementation group: 'io.github.nextpaygroup', name: 'payon-java-sdk', version: '1.0.4'
```

# Code mẫu

- Các thanh số truyền vào hàm PayonHelper
    - `long mcId`: MC_ID - ID Merchant để định danh khách hàng trên PayOn
    - `String appId`: APP_ID - ID ứng dụng để định danh ứng dụng tích hợp
    - `String``ecretKey`: MC_SECRET_KEY - Khóa để thực hiện mã hóa tham số data trong các hàm nghiệp vụ
    - `String url`: URL_API - Đường dẫn API
    - `String httpAuth`: MC_AUTH_USER - Tên Auth basic
    - `StringAuthPass`: MC_AUTH_PASS - Mật khẩu Http Auth basic
- Thanh toán ngay

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);

Map<String, Object> mapParams = new HashMap<>();

mapParams.put("merchant_request_id", merchant_request_id); //Type String: Mã đơn hàng Merchant được tạo từ yêu cầu thanh toán
mapParams.put("amount", 10000);  //Type Int: Giá trị đơn hàng. Đơn vị: VNĐ
mapParams.put("description", "thanh toan"); //Type String: Mô tả thông tin đơn hàng
mapParams.put("url_redirect", "https://payon.vn/"); //Type String: Đường link chuyển tiếp sau khi thực hiện thanh toán thành công
mapParams.put("url_notify", "https://payon.vn/notify"); //Type String: Đường link thông báo kết quả đơn hàng
mapParams.put("url_cancel", "https://payon.vn/cancel"); //Type String: Đường link chuyển tiếp khi khách hàng hủy thanh toán
mapParams.put("customer_fullname", "ran Van A"); //Type String: Họ và tên khách hàng
mapParams.put("customer_email", "tranvana@payon.vn"); //Type String: Địa chỉ email khách hàng
mapParams.put("customer_mobile", "0123456789"); //Type String: Số điện thoại khách hàng

PayonResponse response = helper.createOrderPaynow(mapParams);

if(response.getErrorCode.equals("00"){
    // Call API thành công, tiếp tục xử lý
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Lấy danh sách ngân hàng hỗ trợ thanh toán bằng QR-Code

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);

PayonResponse response = helper.getQrBankCode();

if(response.getErrorCode.equals("00"){
    // Call API thành công, tiếp tục xử lý
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Tạo yêu cầu thanh toán bằng QR-Code

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);

Map<String, Object> mapParams = new HashMap<>();

mapParams.put("merchant_request_id", merchant_request_id); //Type String: Mã đơn hàng Merchant được tạo từ yêu cầu thanh toán
mapParams.put("amount", 10000);  //Type Int: Giá trị đơn hàng. Đơn vị: VNĐ
mapParams.put("description", "thanh toan"); //Type String: Mô tả thông tin đơn hàng
mapParams.put("url_redirect", "https://payon.vn/"); //Type String: Đường link chuyển tiếp sau khi thực hiện thanh toán thành công
mapParams.put("url_notify", "https://payon.vn/notify"); //Type String: Đường link thông báo kết quả đơn hàng
mapParams.put("url_cancel", "https://payon.vn/cancel"); //Type String: Đường link chuyển tiếp khi khách hàng hủy thanh toán
mapParams.put("customer_fullname", "ran Van A"); //Type String: Họ và tên khách hàng
mapParams.put("customer_email", "tranvana@payon.vn"); //Type String: Địa chỉ email khách hàng
mapParams.put("customer_mobile", "0123456789"); //Type String: Số điện thoại khách hàng

PayonResponse response = helper.createQrCode(mapParams);

if(response.getErrorCode.equals("00"){
    // Call API thành công, tiếp tục xử lý
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Lấy danh sách ngân hàng hỗ trợ trả góp

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);
PayonResponse response = helper.getBankInstallment();

if(response.getErrorCode.equals("00"){
    // Call API thành công, tiếp tục xử lý
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Thông tin phí trả góp

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);

Map<String, Object> mapParams = new HashMap<>();

mapParams.put("amount", 10000);  //Type Int: Giá trị đơn hàng. Đơn vị: VNĐ
mapParams.put("bank_code", "TCB"); //Type String: Mã ngân hàng thanh toán.
mapParams.put("cycles", 3); // Type Int: Số kỳ (tháng) trả góp.
mapParams.put("card_type", "VISA"); //Type String: Loại thẻ thanh toán:VISA, MASTERCARD, JCB.

PayonResponse response = helper.getFee(mapParams);

if(response.getErrorCode.equals("00"){
    // Call API thành công, tiếp tục xử lý
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Tạo yêu cầu thanh toán trả góp

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);

Map<String, Object> mapParams = new HashMap<>();

mapParams.put("merchant_request_id", merchant_request_id); //Type String: Mã đơn hàng Merchant được tạo từ yêu cầu thanh toán
mapParams.put("amount", 10000);  //Type Int: Giá trị đơn hàng. Đơn vị: VNĐ
mapParams.put("description", "thanh toan"); //Type String: Mô tả thông tin đơn hàng
mapParams.put("bank_code", "TCB"); //Type String: Mã ngân hàng thanh toán.
mapParams.put("cycles", 3); // Type Int: Số kỳ (tháng) trả góp.
mapParams.put("card_type", "VISA"); //Type String: Loại thẻ thanh toán:VISA, MASTERCARD, JCB.
mapParams.put("userfee", 1); //Type Int:	Chọn người chịu phí: 1. Người mua chịu phí thanh toán 2. Người bán chịu phí thanh toán.
mapParams.put("url_redirect", "https://payon.vn/"); //Type String: Đường link chuyển tiếp sau khi thực hiện thanh toán thành công
mapParams.put("url_notify", "https://payon.vn/notify"); //Type String: Đường link thông báo kết quả đơn hàng
mapParams.put("url_cancel", "https://payon.vn/cancel"); //Type String: Đường link chuyển tiếp khi khách hàng hủy thanh toán
mapParams.put("customer_fullname", "ran Van A"); //Type String: Họ và tên khách hàng
mapParams.put("customer_email", "tranvana@payon.vn"); //Type String: Địa chỉ email khách hàng
mapParams.put("customer_mobile", "0123456789"); //Type String: Số điện thoại khách hàng

PayonResponse response = helper.createOrderInstallment(mapParams);

if(response.getErrorCode.equals("00"){
    // Call API thành công, tiếp tục xử lý
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Kiểm tra giao dịch để cập nhật trạng thái đơn hàng

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);
PayonResponse response = helper.checkPayment(merchant_request_id);//Type String: Mã đơn hàng Merchant được tạo từ yêu cầu thanh toán

if(response.getErrorCode.equals("00"){
    // Call API thành công, tiếp tục xử lý
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Kiểm tra Request PayOn trả về qua url_notify

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);
boolean response = helper.validateNotify(jsonStringBody, checksum); // jsonStringBody và checksum được trả về qua url

if(response){
    // Request hợp lệ và tiếp tục xử lý
    // Call $payon->CheckPayment($merchant_request_id) để lấy trạng thái đơn hàng mới nhất sau đó sẽ xử lý cập nhật trạng thái đơn hàng
} else {
    //Có lỗi xảy ra check lỗi trả về
}
```

• Bypass SSL_VERIFYPEER

```java
PayonHelper helper = new PayonHelper(mcId, appId, secretKey, url, httpAuth, httpAuthPass);
helper.setSslVerifypeer(false);
```
