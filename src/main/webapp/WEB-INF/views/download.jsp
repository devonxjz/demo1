<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Download</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/download.css">
</head>

<body>
    <div class="download-container">
        <jsp:include page="/header.jsp" />

        <main>
            <h1>Danh Sách Tập Tin Tải Về</h1>

            <c:choose>
                <c:when test="${empty fileList}">
                    <p style="text-align: center; color: #666; margin: 30px 0;">Không có tập tin nào.</p>
                </c:when>
                <c:otherwise>
                    <table class="media-table">
                        <thead>
                            <tr>
                                <th style="width: 60px; text-align: center;">STT</th>
                                <th>Tên tập tin</th>
                                <th style="width: 140px;">Dung lượng</th>
                                <th style="width: 140px; text-align: center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="f" items="${fileList}" varStatus="status">
                                <tr>
                                    <td style="text-align: center;">${status.index + 1}</td>
                                    <td><strong>${f.name}</strong></td>
                                    <td>${f.sizeFormatted}</td>
                                    <td style="text-align: center;">
                                        <a href="${pageContext.request.contextPath}/download?file=${f.name}"
                                            class="btn-download-action">
                                            Download
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </main>

        <jsp:include page="/footer.jsp" />
    </div>
</body>

</html>