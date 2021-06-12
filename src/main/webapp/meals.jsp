<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<section>
    <table border="1" cellpadding="8" cellspacing="0">
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
        </tr>
        <tr>
            <jsp:useBean id="meals" scope="request" type="java.util.List"/>
            <c:forEach items="${meals}" var="meal">
                <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal"/>
                <td>${meal.date}</td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
            </c:forEach>
        </tr>
    </table>
    <br/>
</section>
</body>
</html>