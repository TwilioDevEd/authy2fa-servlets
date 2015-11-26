$(document).ready(function() {
    $("#login-form").submit(function(event) {
        event.preventDefault();

        var data = $(event.currentTarget).serialize();
        authyVerification(data);
    });

    var authyVerification = function (data) {
        $.post("/login", data, function (result) {
            $("#authy-modal").modal({ backdrop: "static" }, "show");

            if (result === "onetouch") {
                $(".auth-onetouch").fadeIn();
                monitorOneTouchStatus();
            } else {
                // This handle the case for OneCode and SoftToken.
                requestAuthyToken();
            }
        });
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
        $.post("/authy/request-token");
    }

    $("#logout").click(function() {
        $("#logout-form").submit();
    });
});