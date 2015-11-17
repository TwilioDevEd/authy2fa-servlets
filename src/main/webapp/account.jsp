<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>2FA with Authy</title>
    <link href="//maxcdn.bootstrapcdn.com/bootswatch/3.3.5/flatly/bootstrap.min.css" rel="stylesheet">
    <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="css/site.css" rel="stylesheet" >
</head>
<body>
    <section id="main" class="container">
    <!-- Nav Bar -->
        <nav class="navbar navbar-default">
            <a class="navbar-brand" href="/">2FA with Authy</a>
            <p id="nav-links" class="navbar-text pull-right">
                Welcome back <a href="#">${name}</a>!
                <a href="#" id="logout">Log out</a>
                <form action="logout" method="post" id="logout-form">
                </form>
            </p>
        </nav>

        <h1>${name}</h1>

        <table>
            <tr>
                <th>E-Mail</th>
                <td>${email}</td>
            </tr>
            <tr>
                <th>Phone</th>
                <td>${phoneNumber}</td>
            </tr>
        </table>
    </section>
    <footer class="container">
        Made with <i class="fa fa-heart"></i> by your pals
        <a href="http://www.twilio.com">@twilio</a>
    </footer>

    <script src="//code.jquery.com/jquery-2.1.4.min.js"></script>
    <script src="javascript/application.js"></script>
</body>
</html>
