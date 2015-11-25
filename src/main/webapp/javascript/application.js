$(document).ready(function() {
    $("#login-form").submit(function(event) {
        event.preventDefault();

        var data = $(event.currentTarget).serialize();
        oneTouchVerification(data);
    });

    var oneTouchVerification = function (data) {
        $.post("/login", data, function (result) {
            $("#authy-modal").modal({ backdrop: "static" }, "show");

            if (result === "onetouch") {
                $(".auth-onetouch").fadeIn();
                monitorOneTouchStatus();
            } else {
                // Handle SMS Authentication
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

    $("#logout").click(function() {
        $("#logout-form").submit();
    });
});