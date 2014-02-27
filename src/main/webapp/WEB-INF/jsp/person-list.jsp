<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Spring Web Demo | Cloud Foundry | Swisscom</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>"/>
  </head>
  <body>
    <h2>Spring Web Demo</h2>

    <form action="person/save" method="post">
      <div>
        <input type="hidden" name="id"/>
        <label for="firstname">Person Firstname</label>
        <input type="text" id="firstname" name="firstname"/>
        <label for="lastname">Person Lastname</label>
        <input type="text" id="lastname" name="lastname"/>
        <input type="submit" value="Submit"/>
      </div>
    </form>

    <table border="1">
      <c:forEach var="person" items="${personList}"><tr>
        <td>${person.firstname}</td>
        <td>${person.lastname}</td>
        <td><input type="button" value="delete" onclick="window.location='person/delete?id=${person.id}'"/></td>
      </tr></c:forEach>
    </table>
  </body>
</html>
