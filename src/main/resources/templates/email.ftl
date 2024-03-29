<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Assessment Submitted</title>
    <style>
            li {
                list-style: none;
                padding-left: 0;
            }
        </style>
</head>

<body>
    <div>
        <h3>Dear ${reviewerGroup},</h3>
        <p>${statusMsg}</p>
        <p><strong>Details:</strong></p>
        <div>${details}</div>
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
