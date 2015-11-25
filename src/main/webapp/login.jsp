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
    <!-- Authy Modal -->
    <div class="modal fade" id="authy-modal">
      <div class='modal-dialog'>
        <div class="modal-content">
          <div class='modal-header'>
            <h4 class='modal-title'>Please Authenticate</h4>
          </div>
          <div class='modal-body auth-onetouch'>
            <div class='help-block'>
              <i class="fa fa-spinner fa-pulse"></i> Waiting for OneTouch Approval ...
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- End Authy Modal -->

    <section id="main" class="container">
        <!-- Nav Bar -->
        <nav class="navbar navbar-default">
            <a class="navbar-brand" href="/">2FA with Authy</a>
            <p id="nav-links" class="navbar-text pull-right">
                <a href="/registration">Sign Up</a> or
                <a href="/login">Log in</a>
            </p>
        </nav>

        <small>${data}</small>

        <h1>Welcome Back!</h1>
        <p>
            You never write. You never call. But we're glad you came, regardless! Be a pal and confirm your username and password, would you?
        </p>

        <form id="login-form">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="text" class="form-control" name="email" id="email" placeholder="you@yourdomain.com">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" name="password" id="password">
            </div>
            <button type="submit" class="btn btn-primary">Log In</button>
        </form>

        <form action="confirm-login" method="post" id="confirm-login">
        </form>
    </section>
    <footer class="container">
        Made with <i class="fa fa-heart"></i> by your pals
        <a href="http://www.twilio.com">@twilio</a>
    </footer>

    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script src="javascript/application.js"></script>
</body>
</html>
