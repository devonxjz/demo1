<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!doctype html>
        <html lang="en">

        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>Danh sách sản phẩm - Devonxjz</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        </head>

        <body>
            <div class="container">
                <jsp:include page="/header.jsp" />

                <h1>Danh sách sản phẩm</h1>
                <p class="intro">Chọn các món hàng bạn muốn mua và thêm vào giỏ hàng bên dưới.</p>

                <table class="product-table">
                    <thead>
                        <tr>
                            <th class="col-name">Tên món hàng</th>
                            <th class="col-price">Giá</th>
                            <th class="col-action">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="product" items="${products}">
                            <tr>
                                <td class="col-name">
                                    <strong>${product.name}</strong>
                                    <div class="product-code">Mã: ${product.code}</div>
                                </td>
                                <td class="col-price">${product.formattedPrice}</td>
                                <td class="col-action">
                                    <form action="${pageContext.request.contextPath}/order" method="post"
                                        class="add-form">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="productCode" value="${product.code}">
                                        <input type="hidden" name="quantity" value="1">
                                        <button type="submit" class="btn-primary">Thêm vào giỏ hàng</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="action-links" style="margin-top: 20px;">
                    <a href="${pageContext.request.contextPath}/order?action=cart" class="btn-link">Xem giỏ hàng & Thanh
                        toán</a>
                </div>

                <jsp:include page="/footer.jsp" />
            </div>
        </body>

        </html>