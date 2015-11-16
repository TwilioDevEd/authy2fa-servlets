<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Authy 2 Factor Authentication - Login</title>
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h1>Login</h1>
    <small>${data}</small>

    <form action="login" method="post">
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" class="form-control" name="password" id="password" placeholder="Password">
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
    </form>
</div>
</body>
</html>