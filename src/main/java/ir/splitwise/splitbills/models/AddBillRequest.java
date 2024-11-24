package ir.splitwise.splitbills.models;

public record AddBillRequest(long groupId,
                             String title,
                             String description,
                             double totalCost,
                             long payer,
                             String items) {//todo what can i do for this
    //{
    // name:"pofak",
    //coount:3,
    // [{
    // "userId":1,
    //"count:2
    // },
    //{"userId":2
    // "count":1
    // }]
    // }
}
