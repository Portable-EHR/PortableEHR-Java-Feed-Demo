/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

$(document).ready( function(){
    // Save server's selected responses
    $(".feedApiOption").each(function(){
        var status = $(this).find(".status");
        var option = $(this).find(".option");
        var endpoint = $(this).data("endpoint");

        option.change(function() {
            status.text("...");
            var request = $.ajax({
                method: "PUT",
                url: endpoint,
                data: {option: option.val()}
            });
            request.done(function (msg) {
                status.text("Saved");
            });
            request.fail(function () {
                status.text("ERROR! Check the logs");
            });
        });
    });

    setInterval(function() {
        var request = $.ajax({
            method: "GET",
            url: "/server/logs",
        });
        request.done(function (msg) {
            $('.serverLog').html(msg);
        });
        request.fail(function (jqXHR, textStatus, errorThrown) {
            console.error("Cannot pull servers log : ", jqXHR, textStatus, errorThrown)
        });
    }, 3000);

    // Make client calls with selected options
    $(".feedHubApiCall").each(function(){
        var status = $(this).find(".status");
        var option = $(this).find(".option");
        var button = $(this).find(".button");
        var endpoint = $(this).data("endpoint");

        button.on("click", function(){
            status.text("...");
            $('#responseViewer').text('');
            var request = $.ajax({
                method: "PUT",
                url: endpoint,
                data: { option: option.val()}
            });
            request.done(function( msg ) {
                status.text("Sent");

                $('#responseViewer').text(msg);
            });
            request.fail(function (msg){
                status.text("ERROR! Check the logs");
                $('#responseViewer').text(msg.responseText);
            });
        });
    });

});
