<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Preview ${cmd.id}</title>
</head>

<body>
<g:each in="${result}">
  <p>${it.value}</p>
  <hr/>
</g:each>
<g:img dir="images" file="aquarius_small.png"/>
</body>
</html>