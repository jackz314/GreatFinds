rating = {
    tooltipTxt: {},

    init: function (ratingId, tooltipId) {
        const ratingIdjq = PrimeFaces.escapeClientId(ratingId);
        const tooltipIdjq = PrimeFaces.escapeClientId(tooltipId);
        const _self = this;
        this.tooltipTxt[tooltipId] = $(tooltipIdjq).find(".ui-tooltip-text").html(),

            $(ratingIdjq).find(".ui-rating-star").each(function () {
                $(this).hover(function (event) {
                        return _self.hoverIn(event, tooltipId)
                    },
                    function (event) {
                        $(tooltipIdjq).hide();
                    } //This is on Hoverout
                );

            });
    },

    hoverIn: function (event, tooltipId) {
        const tooltipIdjq = PrimeFaces.escapeClientId(tooltipId);
        const ratingArray = this.tooltipTxt[tooltipId].split("|");

        const tooltipDiv = $(tooltipIdjq);
        // tooltipDiv.for = event.target;
        tooltipDiv.show();
        // this.setCoordinate(tooltipDiv, event.pageX, event.pageY);
        // this.setCoordinate(tooptipDiv, event.target.getBoundingClientRect().bottom, event.target.getBoundingClientRect().right);
        const currEleIdx = $(event.currentTarget).parent().find(".ui-rating-star").index(event.currentTarget);
        const currTooltip = ratingArray[currEleIdx].trim();
        tooltipDiv.find(".ui-tooltip-text").html(currTooltip);
    },

    setCoordinate: function (tooptipDiv, x, y) {
        var pos = {"left": x, "top": y};
        tooptipDiv.offset(pos);
    }

}