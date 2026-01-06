<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>FluxMall - 주문 상세 (${order.orderNumber})</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/layout/header.jsp" %>

    <main class="container">
        <h1 class="page-title">주문 상세 조회</h1>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <div class="order-detail-container">
            <!-- 주문 기본 정보 -->
            <section class="order-info-section">
                <h2>주문 정보</h2>
                <table class="order-info-table">
                    <tr>
                        <th>주문번호</th>
                        <td><strong>${order.orderNumber}</strong></td>
                    </tr>
                    <tr>
                        <th>주문일시</th>
                        <td><fmt:formatDate value="${order.createdAt}" pattern="yyyy년 MM월 dd일 HH:mm"/></td>
                    </tr>
                    <tr>
                        <th>주문상태</th>
                        <td>
                            <span class="status-badge status-${order.status.name().toLowerCase()}">
                                ${order.status.displayName}
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <th>총 결제금액</th>
                        <td><strong class="final-price"><fmt:formatNumber value="${order.totalPrice}" />원</strong></td>
                    </tr>
                </table>
            </section>

            <!-- 배송지 정보 -->
            <section class="shipping-info-section">
                <h2>배송지 정보</h2>
                <table class="shipping-info-table">
                    <tr>
                        <th>받는 사람</th>
                        <td>${order.shippingAddress}</td>
                    </tr>
                    <!-- shippingAddress에 받는사람과 주소가 함께 들어있다고 가정 (실제로는 분리 가능) -->
                </table>
            </section>

            <!-- 주문 상품 목록 -->
            <section class="order-items-section">
                <h2>주문 상품</h2>
                <table class="order-items-table">
                    <thead>
                        <tr>
                            <th>상품정보</th>
                            <th>가격</th>
                            <th>수량</th>
                            <th>소계</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${items}" var="item">
                            <tr>
                                <td class="product-info">
                                    <img src="https://via.placeholder.com/80x80?text=${item.productName}"
                                         alt="${item.productName}">
                                    <div>
                                        <strong>${item.productName}</strong>
                                        <small>${item.productCategory}</small>
                                    </div>
                                </td>
                                <td><fmt:formatNumber value="${item.price}" />원</td>
                                <td>${item.quantity}개</td>
                                <td><strong><fmt:formatNumber value="${item.price * item.quantity}" />원</strong></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </section>

            <!-- 버튼 -->
            <div class="detail-actions">
                <a href="/order/list" class="btn-back">목록으로 돌아가기</a>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/views/layout/footer.jsp" %>
</body>
</html>