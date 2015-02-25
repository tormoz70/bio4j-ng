Ext.namespace('Ekb');
Ekb.bmenu = {
    groups: [
        {
            title: "Выход",
            handler: function() {
                Ext.MessageBox.confirm('Выход', 'Вы действительно хотите закрыть сеанс?', function(btn){
                    if(btn === 'yes') {
                        Bio.app.logout(function() {
                            alert('OK!');
                        });

                    } else {
                        //some code
                    }
                });
            }
        }
    ]
};
