<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="Thanh toán VNPAY Demo">
        <meta name="author" content="Devonxjz">
        <title>Tạo mới đơn hàng VNPAY</title>
        <!-- Bootstrap core CSS -->
        <link href="${pageContext.request.contextPath}/assets/vnpay/bootstrap.min.css" rel="stylesheet"/>
        <!-- Custom styles for this template -->
        <link href="${pageContext.request.contextPath}/assets/vnpay/jumbotron-narrow.css" rel="stylesheet">      
        <script src="${pageContext.request.contextPath}/assets/vnpay/jquery-1.11.3.min.js"></script>
    </head>

    <body>
        <div class="container">
            <div class="header clearfix" style="border-bottom: 1px solid #e5e5e5; margin-bottom: 20px; padding-bottom: 10px;">
                <nav>
                    <ul class="nav nav-pills pull-right">
                        <li role="presentation"><a href="${pageContext.request.contextPath}/order">Quay lại Shop</a></li>
                        <li role="presentation"><a href="${pageContext.request.contextPath}/vnpay_querydr.jsp">Tra cứu GD</a></li>
                        <li role="presentation"><a href="${pageContext.request.contextPath}/vnpay_refund.jsp">Hoàn tiền</a></li>
                    </ul>
                </nav>
                <h3 class="text-muted"><a href="${pageContext.request.contextPath}/" style="text-decoration: none; color: #333;">VNPAY DEMO - DEMO1</a></h3>
            </div>
            <h3>Tạo mới đơn hàng thanh toán VNPAY</h3>
            <div class="table-responsive">
                <form action="${pageContext.request.contextPath}/vnpayajax" id="frmCreateOrder" method="post">        
                    <div class="form-group">
                        <label for="amount">Số tiền (VNĐ)</label>
                        <input class="form-control" data-val="true" data-val-number="The field Amount must be a number." data-val-required="The Amount field is required." id="amount" max="100000000" min="1" name="amount" type="number" value="${not empty param.amount ? param.amount : 10000}" />
                    </div>
                    
                    <div class="form-group">
                        <label for="orderInfo">Nội dung thanh toán</label>
                        <input class="form-control" id="orderInfo" name="orderInfo" type="text" value="${not empty param.orderInfo ? param.orderInfo : 'Thanh toan don hang demo1'}" />
                    </div>

                    <h4>Chọn phương thức thanh toán</h4>
                    <div class="form-group">
                        <h5>Cách 1: Chuyển hướng sang Cổng VNPAY chọn phương thức thanh toán</h5>
                        <label>
                            <input type="radio" checked="true" id="bankCodeDefault" name="bankCode" value="">
                            Cổng thanh toán VNPAYQR
                        </label><br>
                       
                        <h5>Cách 2: Tách phương thức tại site của đơn vị kết nối</h5>
                        <label>
                            <input type="radio" id="bankCodeVnpayQr" name="bankCode" value="VNPAYQR">
                            Thanh toán bằng ứng dụng hỗ trợ VNPAYQR
                        </label><br>
                       
                        <label>
                            <input type="radio" id="bankCodeVnBank" name="bankCode" value="VNBANK">
                            Thanh toán qua thẻ ATM/Tài khoản nội địa
                        </label><br>
                       
                        <label>
                            <input type="radio" id="bankCodeIntCard" name="bankCode" value="INTCARD">
                            Thanh toán qua thẻ quốc tế (Visa, Master, JCB)
                        </label><br>
                    </div>

                    <div class="form-group">
                        <h5>Chọn ngôn ngữ giao diện thanh toán:</h5>
                        <label>
                            <input type="radio" id="langVn" checked="true" name="language" value="vn">
                            Tiếng Việt
                        </label>&nbsp;&nbsp;
                        <label>
                            <input type="radio" id="langEn" name="language" value="en">
                            Tiếng Anh
                        </label>
                    </div>

                    <button type="submit" class="btn btn-primary btn-lg" style="margin-top: 15px;">Thanh toán VNPAY</button>
                    <a href="${pageContext.request.contextPath}/order?action=checkout" class="btn btn-default btn-lg" style="margin-top: 15px; margin-left: 10px;">Về giỏ hàng</a>
                </form>
            </div>
            <p>&nbsp;</p>
            <footer class="footer" style="padding-top: 19px; color: #777; border-top: 1px solid #e5e5e5;">
                <p>&copy; VNPAY 2026 - Demo1 Integration</p>
            </footer>
        </div>
          
        <link href="https://pay.vnpay.vn/lib/vnpay/vnpay.css" rel="stylesheet" />
        <script src="https://pay.vnpay.vn/lib/vnpay/vnpay.min.js"></script>
        <script type="text/javascript">
            $("#frmCreateOrder").submit(function (e) {
                var postData = $("#frmCreateOrder").serialize();
                var submitUrl = $("#frmCreateOrder").attr("action");
                $.ajax({
                    type: "POST",
                    url: submitUrl,
                    data: postData,
                    dataType: 'json',
                    success: function (x) {
                        if (x.code === '00') {
                            if (window.vnpay) {
                                vnpay.open({width: 768, height: 600, url: x.data});
                            } else {
                                location.href = x.data;
                            }
                            return false;
                        } else {
                            alert(x.message || "Lỗi tạo link thanh toán");
                        }
                    },
                    error: function() {
                        // Fallback: submit standard form if ajax call fails
                        $("#frmCreateOrder").unbind('submit').submit();
                    }
                });
                return false;
            });
        </script>       
    </body>
</html>
