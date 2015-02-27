Ext.namespace('Ekb');
Ekb.bmenu = {
    groups: [
        {
            title: "Выход",
            handler: function() {
                Ext.MessageBox.confirm('Выход', 'Вы действительно хотите завершить сеанс?', function(btn){
                    if(btn === 'yes') {
                        Bio.app.logout(function() {
                            alert('Сеанс завершен!');
                        });

                    } else {
                        //some code
                    }
                });
            }
        }
    ]
};
