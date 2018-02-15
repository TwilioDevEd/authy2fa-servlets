$(document).ready(function() {
    $("#login-form").submit(function(event) {
        event.preventDefault();

        var data = $(event.currentTarget).serialize();
        authyVerification(data);
    });

    var authyVerification = function (data) {
        $.post("/login", data, function (result) {
            resultJson = JSON.parse(result);
            resultActions[resultJson.result](resultJson.message);
        });
    };

    var resultActions = {
        ONETOUCH: function() {
            $("#authy-modal").modal({ backdrop: "static" }, "show");
            $(".auth-token").hide();
            $(".auth-onetouch").fadeIn();
            monitorOneTouchStatus();
        },

        SMS: function () {
            $("#authy-modal").modal({ backdrop: "static" }, "show");
            $(".auth-onetouch").hide();
            $(".auth-token").fadeIn();
            requestAuthyToken();
        },

        ERROR: function (message) {
            $("#error-message").text(message);
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
            .done(function () {
                $("#authy-token").removeAttr("disabled");
            });
    }

    $("#logout").click(function() {
        $("#logout-form").submit();
    });
});