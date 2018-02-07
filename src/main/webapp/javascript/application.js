$(document).ready(function() {
    $("#login-form").submit(function(event) {
        event.preventDefault();

        var data = $(event.currentTarget).serialize();
        authyVerification(data);
    });

    var authyVerification = function (data) {
        $.post("/login", data, function (result) {
            resultActions[result]();
        });
    };

    var resultActions = {
        onetouch: function() {
            $("#authy-modal").modal({ backdrop: "static" }, "show");
            $(".auth-token").hide();
            $(".auth-onetouch").fadeIn();
            monitorOneTouchStatus();
        },

        sms: function () {
            $("#authy-modal").modal({ backdrop: "static" }, "show");
            $(".auth-onetouch").hide();
            $(".auth-token").fadeIn();
            requestAuthyToken();
        },

        unauthorized: function () {
            $("#error-message").text("Invalid credentials");
        },

        unexpectedError: function () {
            $("#error-message").text("Unexpected error. Check the logs");
        }
    };

    var monitorOneTouchStatus = function () {
        $.post("/authy/status")
            .done(function (data) {
                if (data === "") {
                    setTimeout(monitorOneTouchStatus, 2000);
                } else {
                    $("#confirm-login").submit();
                }
            });
    }

    var requestAuthyToken = function () {
        $.post("/authy/request-token")
            .done(function (data) {
                $("#authy-token-label").text(data);
            });
    }

    $("#logout").click(function() {
        $("#logout-form").submit();
    });
});