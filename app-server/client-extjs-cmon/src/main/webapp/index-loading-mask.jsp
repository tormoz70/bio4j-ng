<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="vars.jsp"%>
<div id="loading-mask" style=""></div>
<div id="loading">
    <div class="loading-indicator">
        <img src="<%=APP_URL%>/shared/images/extanim32.gif" width="32" height="32"
             style="margin-right:8px;float:left;vertical-align:top;"/><%=APP_TITLE%>
        <br/><span id="loading-msg">Загрузка стилей...</span>
    </div>
</div>
