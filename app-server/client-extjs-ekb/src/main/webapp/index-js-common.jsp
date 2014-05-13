<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <div id="code-load" style="display:none;">
        <script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Загрузка Core API...';</script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/extjs/ext-all-debug.js"></script>
        <script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Загрузка UI Components...';</script>
        <script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Инициализация...';</script>

        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-ext-overrides.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-rest-proxy.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-rest-reader.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-rest-writer.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-data-store.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-request.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-grid-panel.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-form-combo.js"></script>

        <%--<script type="text/javascript" src="../../../biojs/jquery-1.11.0.js"></script>--%>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-cookies.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-dlg.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-tools.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-login.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/biojs/bio-app.js"></script>


    </div>
