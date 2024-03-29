<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <style>
            li {
                list-style: none;
                padding-left: 0;
            }
        </style>
</head>

<body>
    <div>
        <h3>Dear ${user},</h3>
          <p>${message}</p>
        <p><strong>Details:</strong></p>
        <table border="1">
                <tr><th> Category</th><th>Value</th></tr>
                <tr><td> Project Code </td><td>${projectcode}</td></tr>
                <tr><td> Project Name </td><td>${projectname}</td></tr>
                <tr><td> Account Name </td><td>${accountname}</td></tr>
                <tr><td> Project Type </td><td>${projecttype}</td></tr>
                <tr><td> Template Name </td><td>${templatename}</td></tr>
                 <tr><td> Due Date </td><td>${duedate}</td></tr>
             </table>
    </div>
    </br>
    Visit <a href="${eedashboardurl}">EE Dashboard</a> for more details.
    </br>
</body>
<footer>
</br>
Thank You,
<div>
EE Dashboard
</div>
<div>
</br>
-- This is an automatically generated email – please do not reply to it.  Visit <a href="${eedashboardurl}">EE Dashboard</a> for help & support --
</div>
</footer>
</html>
