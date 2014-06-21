<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@include file="vars.jsp" %>

    <div id="code-load" style="display:none;">
        <script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Загрузка Core API...';</script>
        <script type="text/javascript" src="<%=APP_URL%>/<%=EXTJS_PATH%>/ext-all-debug.js"></script>

        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-user.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-app.js"></script>

        <script type="text/javascript">
            Ext.QuickTips.init();
            Ext.form.Field.prototype.msgTarget = 'side';
            Ext.enableListenerCollection = true;

            Bio.app = new Bio.Application({
                APP_URL: "<%=APP_URL%>",
                APP_TITLE: "<%=APP_TITLE%>",
                сurUsr: null
            });
        </script>

        <script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Загрузка UI Components...';</script>
        <script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Инициализация...';</script>

        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-ext-overrides.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-rest-proxy.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-rest-reader.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-rest-writer.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-data-store.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-request.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-grid-panel.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-form-combo.js"></script>

        <%--<script type="text/javascript" src="../../../biojs/jquery-1.11.0.js"></script>--%>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-cookies.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/dialog/bio-dialog-base.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/dialog/bio-dialog-message.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/dialog/bio-dialog-login.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-dlg.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-tools.js"></script>
        <script type="text/javascript" src="<%=APP_URL%>/biojs/bio-login.js"></script>


    </div>
