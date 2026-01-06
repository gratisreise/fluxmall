<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>FluxMall - 장바구니</title>
    <link rel="stylesheet" href="/css/style.css">
    <script>
        // 수량 수정 AJAX
        function updateQuantity(cartItemId, newQuantity) {
            if (newQuantity < 1) {
                if (confirm('상품을 삭제하시겠습니까?')) {
                    deleteItem(cartItemId);
                }
                return;
            }

            fetch('/cart/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-CSRF-TOKEN': '${_csrf.token}'
                },
                body: 'cartItemId=' + cartItemId + '&quantity=' + newQuantity
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('totalPrice').textContent =
                        data.totalPrice.toLocaleString() + '원';
                }
            });
        }

        // 개별 삭제 AJAX
        function deleteItem(cartItemId) {
            if (!confirm('상품을 장바구니에서 삭제하시겠습니까?')) return;

            fetch('/cart/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-CSRF-TOKEN': '${_csrf.token}'
                },
                body: 'cartItemId=' + cartItemId
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('cartItem-' + cartItemId).remove();
                    document.getElementById('totalPrice').textContent =
                        data.totalPrice.toLocaleString() + '원';
                    document.getElementById('cartCount').textContent = data.cartItemCount;

                    // 장바구니 비었으면 메시지 표시
                    if (data.cartItemCount === 0) {
                        document.querySelector('.cart-table').style.display = 'none';
                        document.querySelector('.empty-cart').style.display = 'block';
                    }
                }
            });
        }

        // 전체 선택 체크박스
        function toggleAll(source) {
            document.querySelectorAll('.item-checkbox').forEach(cb => {
                cb.checked = source.checked;
            });
        }
    </script>
</head>
<body>
    <%@ include file="/WEB-INF/views/layout/header.jsp" %>

    <main class="container">
        <h1 class="page-title">장바구니</h1>

        <c:choose>
            <c:when test="${empty cartItems}">
                <div class="empty-cart">
                    <p>장바구니에 담긴 상품이 없습니다.</p>
                    <a href="/products" class="btn-primary">쇼핑 계속하기</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="cart-table-container">
                    <table class="cart-table">
                        <thead>
                            <tr>
                                <th><input type="checkbox" id="selectAll" onclick="toggleAll(this)"></th>
                                <th>상품정보</th>
                                <th>가격</th>
                                <th>수량</th>
                                <th>소계</th>
                                <th>삭제</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${cartItems}" var="item">
                                <tr id="cartItem-${item.id}">
                                    <td><input type="checkbox" class="item-checkbox"></td>
                                    <td class="product-info">
                                        <a href="/products/${item.productId}">
                                            <img src="https://via.placeholder.com/80x80?text=${item.productName}"
                                                 alt="${item.productName}">
                                            <span>${item.productName}</span>
                                        </a>
                                        <small>${item.productCategory}</small>
                                    </td>
                                    <td class="price"><fmt:formatNumber value="${item.productPrice}" />원</td>
                                    <td class="quantity">
                                        <button onclick="updateQuantity(${item.id}, ${item.quantity - 1})">-</button>
                                        <input type="text" value="${item.quantity}" readonly>
                                        <button onclick="updateQuantity(${item.id}, ${item.quantity + 1})">+</button>
                                    </td>
                                    <td class="subtotal">
                                        <strong><fmt:formatNumber value="${item.productPrice * item.quantity}" />원</strong>
                                    </td>
                                    <td>
                                        <button onclick="deleteItem(${item.id})" class="btn-delete">삭제</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="cart-summary">
                    <div class="total-price-box">
                        <span>총 결제 예정 금액</span>
                        <strong id="totalPrice"><fmt:formatNumber value="${totalPrice}" />원</strong>
                    </div>
                    <a href="/order/checkout" class="btn-order-all">전체 주문하기</a>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <%@ include file="/WEB-INF/views/layout/footer.jsp" %>
</body>
</html>