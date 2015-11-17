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
              <a href="#users">Sign Up</a> or
              <a href="/login">Log in</a>
          </p>
        </nav>

        <h1>We're going to be *BEST* friends</h1>
        <p> Thanks for your interest in signing up! Can you tell us a bit about yourself?</p>

        <form action="registration" method="post">
          <div class="form-group">
            <label for="name">Tell us your name:</label>
            <input type="text" class="form-control" name="name" id="name" placeholder="Zingelbert Bembledack" value="${name}">
            <span class="text-danger">${nameError}</span>
          </div>
          <div class="form-group">
            <label for="email">Enter Your E-mail Address:</label>
            <input type="text" class="form-control" name="email" id="email" placeholder="you@yourdomain.com" value="${email}">
            <span class="text-danger">${emailError}</span>
            <span class="text-danger">${emailInvalidError}</span>
          </div>
          <div class="form-group">
            <label for="password">Enter a password:</label>
            <input type="password" class="form-control" name="password" id="password">
            <span class="text-danger">${passwordError}</span>
          </div>
          <div class="form-group">
            <label for="authy-countries">Country code:</label>
            <input type="text" class="form-control" name="countryCode" id="authy-countries" value="${countryCode}">
            <span class="text-danger">${countryCodeError}</span>
          </div>
          <div class="form-group">
            <label for="phoneNumber">Phone number:</label>
            <input type="text" class="form-control" name="phoneNumber" id="phoneNumber" value="${phoneNumber}">
            <span class="text-danger">${phoneNumberError}</span>
          </div>
          <button class="btn btn-primary">Sign up</button>
        </form>
    </section>
    <footer class="container">
        Made with <i class="fa fa-heart"></i> by your pals
        <a href="http://www.twilio.com">@twilio</a>
    </footer>
</body>
</html>
