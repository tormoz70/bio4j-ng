<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="vars.jsp"%>
<head>
    <title><%=APP_TITLE%></title>
    <meta http-equiv="CONTENT-TYPE" content="TEXT/HTML; CHARSET=UTF-8">
    <link rel="stylesheet" type="text/css" href="<%=APP_URL%>/<%=EXTJS_PATH%>/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="<%=APP_URL%>/welcome/css/extjs.css"/>
    <style type="text/css">

        #loading-mask{
            background-color:white;
            height:100%;
            position:absolute;
            left:0;
            top:0;
            width:100%;
            z-index:20000;
        }
        #loading{
            height:auto;
            position:absolute;
            left:45%;
            top:40%;
            padding:2px;
            z-index:20001;
        }
        #loading a {
            color:#225588;
        }
        #loading .loading-indicator{
            background:white;
            color:#444;
            font:bold 13px Helvetica, Arial, sans-serif;
            height:auto;
            margin:0;
            padding:10px;
        }
        #loading-msg {
            font-size: 10px;
            font-weight: normal;
        }

        #root-container {

        }
        #root-container dd {
            cursor:pointer;
            float:left;
            height:100px;
            margin:5px 5px 5px 10px;
            width:300px;
            zoom:1;
        }
        #root-container dd img {
            border: 1px solid #ddd;
            float:left;
            height:90px;
            margin:5px 0 0 5px;
            width:120px;
        }

        #root-container dd div {
            float:left;
            margin-left:10px;
            width:160px;
        }

        #root-container dd h4 {
            color:#555;
            font-size:11px;
            font-weight:bold;
        }

        #root-container dd p {
            color:#777;
        }

        #root-container dd.over {
            background: #F5FDE3 url(shared/images/bsrv-over.gif) no-repeat;
        }

        #root-container {
            background-color: #fff;
            border:1px solid;
            border-color:#dadada #ebebeb #ebebeb #dadada;
            overflow: auto;
        }

        #bsrv-ct {
            padding:2px;
            font: 11px tahoma,arial,helvetica,sans-serif
        }

        #root-container h2 {
            border-bottom: 2px solid #99bbe8;
            cursor:pointer;
            padding-top:6px;
        }
        #root-container h2 div {
            background:transparent url(<%=EXTJS_PATH%>/resources/themes/images/default/grid/group-expand-sprite.gif) no-repeat 3px -47px;
            color:#3764a0;
            font:bold 11px Helvetica, Arial, sans-serif;
            padding:4px 4px 4px 17px;
        }
        #root-container .collapsed h2 div {
            background-position: 3px 3px;
        }
        #root-container .collapsed dl {
            display:none;
        }
        li {
            list-style: inherit !important;
        }
        #hd {
            height: auto;
        }

    </style>
</head>
