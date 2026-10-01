<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.TimeZone"%>
<%
    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String currentDate = formatter.format(cld.getTime());
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Hoàn Tiền Giao Dịch VNPAY (Refund)</title>
        <!-- Bootstrap core CSS -->
        <link href="${pageContext.request.contextPath}/assets/vnpay/bootstrap.min.css" rel="stylesheet"/>
        <!-- Custom styles for this template -->   
        <link href="${pageContext.request.contextPath}/assets/vnpay/jumbotron-narrow.css" rel="stylesheet"> 
        <script src="${pageContext.request.contextPath}/assets/vnpay/jquery-1.11.3.min.js"></script>
    </head>

    <body>
        <div class="container" style="max-width: 720px; margin-top: 30px;">
            <div class="header clearfix" style="border-bottom: 1px solid #e5e5e5; padding-bottom: 15px; margin-bottom: 25px;">
                <nav>
                    <ul class="nav nav-pills pull-right">
                        <li role="presentation"><a href="${pageContext.request.contextPath}/order">Shop</a></li>
                        <li role="presentation"><a href="${pageContext.request.contextPath}/vnpay_pay.jsp">Tạo Giao Dịch</a></li>
                        <li role="presentation"><a href="${pageContext.request.contextPath}/vnpay_querydr.jsp">Tra Cứu GD</a></li>
                    </ul>
                </nav>
                <h3 class="text-muted"><a href="${pageContext.request.contextPath}/" style="text-decoration: none; color: #333;">VNPAY REFUND</a></h3>
            </div>
            <h3>Yêu cầu hoàn tiền giao dịch (Refund)</h3>
            <div class="table-responsive">
                <form action="${pageContext.request.contextPath}/vnpayrefund" id="frmRefund" method="post">
                    <div class="form-group">
                        <label for="order_id">Mã giao dịch cần hoàn (vnp_TxnRef):</label>
                        <input class="form-control" id="order_id" name="order_id" type="text" placeholder="Nhập mã giao dịch cần hoàn tiền" required/>
                    </div>
                    <div class="form-group">
                        <label for="amount">Số tiền hoàn (VNĐ):</label>
                        <input class="form-control" id="amount" name="amount" type="number" min="1000" max="100000000" value="10000" required/>
                    </div>
                    <div class="form-group">
                        <label for="trantype">Loại hoàn tiền:</label>
                        <select name="trantype" id="trantype" class="form-control">
                            <option value="02">02 - Hoàn tiền toàn phần</option>
                            <option value="03">03 - Hoàn tiền một phần</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="trans_date">Thời gian khởi tạo giao dịch ban đầu (vnp_CreateDate):</label>
                        <input class="form-control" id="trans_date" name="trans_date" type="text" placeholder="yyyyMMddHHmmss" value="<%= currentDate %>" required/>
                        <small class="text-muted">Định dạng chuẩn: yyyyMMddHHmmss (Ví dụ: <%= currentDate %>)</small>
                    </div>
                    <div class="form-group">
                        <label for="user">Người thực hiện hoàn tiền (User/Admin):</label>
                        <input class="form-control" id="user" name="user" type="text" value="Admin" required/>
                    </div>
                    <div class="form-group" style="margin-top: 20px;">
                        <button type="submit" class="btn btn-warning btn-lg" id="btnSubmit">Thực Hiện Hoàn Tiền</button>
                        <a href="${pageContext.request.contextPath}/vnpay_pay.jsp" class="btn btn-default btn-lg">Trở về</a>
                    </div>
                </form>   

                <div id="refundResultArea" style="margin-top: 25px; display: none;">
                    <h4>Kết quả phản hồi từ VNPAY:</h4>
                    <pre id="refundResultContent" style="background: #272822; color: #f8f8f2; padding: 15px; border-radius: 6px; font-family: monospace;"></pre>
                </div>
            </div>
            <footer class="footer" style="padding-top: 19px; color: #777; border-top: 1px solid #e5e5e5; margin-top: 40px;">
                <p>&copy; VNPAY 2026 - Demo1 Refund Tool</p>
            </footer>
        </div>

        <script type="text/javascript">
            $("#frmRefund").submit(function(e) {
                e.preventDefault();
                $("#btnSubmit").prop("disabled", true).text("Đang gửi yêu cầu...");
                $("#refundResultArea").hide();
                $.ajax({
                    type: "POST",
                    url: $(this).attr("action"),
                    data: $(this).serialize(),
                    dataType: "text",
                    success: function(resp) {
                        $("#btnSubmit").prop("disabled", false).text("Thực Hiện Hoàn Tiền");
                        try {
                            var json = JSON.parse(resp);
                            $("#refundResultContent").text(JSON.stringify(json, null, 2));
                        } catch (e) {
                            $("#refundResultContent").text(resp);
                        }
                        $("#refundResultArea").fadeIn();
                    },
                    error: function(err) {
                        $("#btnSubmit").prop("disabled", false).text("Thực Hiện Hoàn Tiền");
                        $("#refundResultContent").text("Lỗi kết nối hoặc xử lý yêu cầu: " + JSON.stringify(err));
                        $("#refundResultArea").fadeIn();
                    }
                });
            });
        </script>
    </body>
</html>
