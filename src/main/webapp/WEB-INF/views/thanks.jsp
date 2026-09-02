<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Survey Confirmation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="container">
        <div class="logo">
            <img src="${pageContext.request.contextPath}/assets/images/logo.jpg" alt="Devonxjz Logo" width="72" height="72">
        </div>
        <h1>Thanks for taking our survey!</h1>
        <p class="intro">Here is the information that you entered:</p>
        <table class="result-table">
            <tr><td class="label-col">First Name:</td><td>${user.firstName}</td></tr>
            <tr><td class="label-col">Last Name:</td><td>${user.lastName}</td></tr>
            <tr><td class="label-col">Email:</td><td>${user.email}</td></tr>
            <tr><td class="label-col">Date of Birth:</td><td>${user.dateOfBirth}</td></tr>
            <tr><td class="label-col">Heard From:</td><td>${user.heardFrom}</td></tr>
            <tr><td class="label-col">Updates:</td><td>${user.wantsUpdates}</td></tr>
            <tr><td class="label-col">Email Offers:</td><td>${user.emailAnnouncements}</td></tr>
            <tr><td class="label-col">Contact Via:</td><td>${user.contactBy}</td></tr>
        </table>
        <div class="button-row">
            <a class="btn-link" href="${pageContext.request.contextPath}/index.html">Return to Survey</a>
        </div>
    </div>
</body>
</html>
