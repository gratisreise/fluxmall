<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>FluxMall - 로그인</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/layout/header.jsp" %>

    <main class="container">
        <div class="login-container">
            <h2 class="page-title">로그인</h2>

            <c:if test="${not empty error}">
                <div class="alert error">${error}</div>
            </c:if>

            <!-- Spring Security가 처리하는 로그인 폼 -->
            <form action="/member/login" method="post" class="login-form">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="form-group">
                    <label for="username">아이디</label>
                    <input type="text" id="username" name="username" required placeholder="아이디를 입력하세요" autofocus>
                </div>

                <div class="form-group">
                    <label for="password">비밀번호</label>
                    <input type="password" id="password" name="password" required placeholder="비밀번호를 입력하세요">
                </div>

                <button type="submit" class="btn-login">로그인</button>
            </form>

            <div class="login-links">
                <a href="/member/register">회원가입</a>
                <!-- 나중에 추가할 기능 -->
                <!-- <span>|</span> <a href="#">아이디 찾기</a> <span>|</span> <a href="#">비밀번호 찾기</a> -->
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/views/layout/footer.jsp" %>
</body>
</html>