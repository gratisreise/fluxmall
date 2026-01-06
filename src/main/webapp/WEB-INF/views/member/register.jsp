
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>FluxMall - 회원가입</title>
    <link rel="stylesheet" href="/css/style.css">
    <script>
      // 실시간 아이디 중복 체크 (AJAX)
      function checkUsername() {
        const username = document.getElementById('username').value.trim();
        const msg = document.getElementById('usernameMsg');

        if (username.length < 4) {
          msg.textContent = '아이디는 4자 이상 입력해주세요.';
          msg.style.color = 'red';
          return;
        }

        fetch('/member/checkUsername?username=' + encodeURIComponent(username))
        .then(response => response.json())
        .then(duplicate => {
          if (duplicate) {
            msg.textContent = '이미 사용 중인 아이디입니다.';
            msg.style.color = 'red';
          } else {
            msg.textContent = '사용 가능한 아이디입니다.';
            msg.style.color = 'green';
          }
        })
        .catch(() => {
          msg.textContent = '서버 오류';
          msg.style.color = 'red';
        });
      }
    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="container">
    <div class="register-container">
        <h2 class="page-title">회원가입</h2>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <form action="/member/register" method="post" class="register-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="form-group">
                <label for="username">아이디 <span class="required">*</span></label>
                <input type="text" id="username" name="username" required minlength="4" maxlength="20"
                       placeholder="4~20자 영문/숫자" onblur="checkUsername()">
                <small id="usernameMsg" class="help-text"></small>
            </div>

            <div class="form-group">
                <label for="password">비밀번호 <span class="required">*</span></label>
                <input type="password" id="password" name="password" required minlength="6"
                       placeholder="6자 이상">
            </div>

            <div class="form-group">
                <label for="nickname">닉네임 <span class="required">*</span></label>
                <input type="text" id="nickname" name="nickname" required maxlength="15"
                       placeholder="1~15자">
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-register">회원가입 완료</button>
                <a href="/member/login" class="btn-cancel">취소</a>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
</body>
</html>
