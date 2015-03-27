Ext.namespace("Bio");
// name - name of the cookie
// value - value of the cookie
// [expires] - expiration date of the cookie (defaults to end of current session)
// [path] - path for which the cookie is valid (defaults to path of calling document)
// [domain] - domain for which the cookie is valid (defaults to domain of calling document)
// [secure] - Boolean value indicating if the cookie transmission requires a secure transmission
// * an argument defaults when it is assigned null as a placeholder
// * a null placeholder is not required for trailing omitted arguments
Bio.cooks = {
	setCookie:function(name, value) {
        var expires = new Date();
        expires.setMonth(expires.getMonth()+1);

        var path = Bio.app.APP_URL,
            keyVal = name + "=" + encodeURI(value),
            curCookie = keyVal +
                "; expires=" + expires.toGMTString() +
                "; path=" + path;

        if (keyVal.length <= 4000)
          document.cookie = curCookie;

//        var path = Bio.app.APP_URL;
//        Ext.util.Cookies.set(name, value, cdt, path);
	},

// name - name of the desired cookie
// * return string containing value of specified cookie or null if cookie does not exist
	getCookie:function(name) {
        var prefix = name + "=";
        var cookieStartIndex = document.cookie.indexOf(prefix);
        if (cookieStartIndex == -1) return null;
        var cookieEndIndex = document.cookie.indexOf(";", cookieStartIndex + prefix.length);
        if (cookieEndIndex == -1)
          cookieEndIndex = document.cookie.length;
        return decodeURI(document.cookie.substring(cookieStartIndex + prefix.length, cookieEndIndex));

        //var path = Bio.app.APP_URL;
        //return Ext.util.Cookies.get(name);
	},

// name - name of the cookie
// [path] - path of the cookie (must be same as path used to create cookie)
// [domain] - domain of the cookie (must be same as domain used to create cookie)
// * path and domain default if assigned null or omitted if no explicit argument proceeds
	deleteCookie:function(name) {
        if (this.getCookie(name)) {
          var path = Bio.app.APP_URL
          document.cookie = name + "=" +
	          ((path) ? "; path=" + path : "") +
	          "; expires=Thu, 01-Jan-70 00:00:01 GMT";
        }
//        var path = Bio.app.APP_URL
//        Ext.util.Cookies.clear(name, path);
    },

// date - any instance of the Date object
// * you should hand all instances of the Date object to this function for "repairs"
// * this function is taken from Chapter 14, "Time and Date in JavaScript", in "Learn Advanced JavaScript Programming"
	fixDate:function(date) {
        var base = new Date(0)
        var skew = base.getTime()
        if (skew > 0)
          date.setTime(date.getTime() - skew)
	}
}	
