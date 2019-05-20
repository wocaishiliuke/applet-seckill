<%-- 该tag用于格式化java8的LocalDatime,解决jstl不支持java8时间的问题 --%>
<%@ tag body-content="empty" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- 定义该tag的标签属性，<tags:localDataTime dateTime="${sk.createTIme}"/> --%>
<%@ attribute name="dateTime" required="true" type="java.time.LocalDateTime" %>
<%@ attribute name="pattern" required="false" type="java.lang.String" %>

<%-- 先判断日期时间转换规则是否存在，不存在给出如下的默认规则 --%>
<c:if test="${empty pattern}">
    <c:set var="pattern" value="yyyy-MM-dd HH:mm:ss"/>
</c:if>

<%-- 获取jsp页面传入的日期时间, 格式为【2017-5-26T13:59:12】 --%>
<c:set var="datetime" value="${dateTime}"/>
<%-- 定义变量time=datetime中'T'后面的时间串（时:分:秒）  --%>
<c:set var="time" value="${fn:substringAfter(datetime, 'T')}"/>
<%-- 定义变量timeLength=time的长度 --%>
<c:set var="timeLength" value="${fn:length(time)}"/>
<%-- 定义变量generalLength, 值为字符串【123456】的长度（6） --%>
<c:set var="generalLength" value="${fn:length('123456')}"/>
<%-- 定义字符串变量cloneZero，值为:00 --%>
<c:set var="cloneZero" value=":00"/>

<%-- 当（时:分:秒）不足6位时，说明缺少秒，这里补充:00 --%>
<c:if test="${timeLength lt generalLength}">
    <%-- 拼接补充秒数（EL中"+"为相加，非拼接字符串） --%>
    <c:set var="datetimeCloneZero" value="${datetime}${cloneZero}"/>
    <%-- 将java8日期时间中的T，替换成一个空字符串 --%>
    <c:set var="cleandDateTime" value="${fn:replace(datetimeCloneZero,'T',' ')}"/>
</c:if>
<%-- 当页面传过来的时间大于6位时，说明时间时完整的，不需要填充:00，直接T替换为空串即可 --%>
<c:if test="${timeLength gt generalLength}">
    <c:set var="cleandDateTime" value="${fn:replace(datetime,'T',' ')}"/>
</c:if>

<%-- 解析时间, type="BOTH"表示同时解析日期和时间 --%>
<fmt:parseDate value="${cleandDateTime}" var="parsedDateTime" pattern="${pattern}" type="BOTH"/>
<fmt:formatDate value="${parsedDateTime}" pattern="${pattern}" type="BOTH"/>