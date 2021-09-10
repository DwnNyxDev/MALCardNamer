//start of data
//start of code

var docs = app.documents;

for(var user in users){
    if(users[user].cards.length>0){
        var user_folder = Folder(save_location+"/"+users[user].names[0]);
        if(!user_folder.exists){
            user_folder.create();
        }
        
        for(var u=0; u<users[user].cards.length; u++){
            var card = users[user].cards[u];
            var cLimit=20;
            var replaceString="Name";
            for(var psdFind in psds){
                if(psds[psdFind].name==card){
                    cLimit=psds[psdFind].limit;
                    replaceString=psds[psdFind].replace;
                    break;
                }
            }
            var user_name = users[user].names[0];
            if(user_name.length>cLimit&&users[user].names.length>1){
                user_name=users[user].names[1];
            }
            var card_file = new File(user_folder+"/"+user_name+"_"+card.replace(".psd","")+".png");
            if(!card_file.exists){
                app.activeDocument = documents.getByName(card);
                app.activeDocument = app.activeDocument.duplicate();
                renameLayers(app.activeDocument);
                function renameLayers(l){
                    for(var i=0; i<l.layers.length;i++){
                        var layer= l.layers[i];
                        if(layer.layers!=null){
                            renameLayers(layer);
                        }
                        if(layer.kind == LayerKind.TEXT){
                            layer.textItem.contents=layer.textItem.contents.replace(replaceString,user_name);
                        }
                    }
                }
                var pngOpts = new PNGSaveOptions;
                app.activeDocument.saveAs(card_file,pngOpts,true,Extension.LOWERCASE);
                app.activeDocument.close(SaveOptions.DONOTSAVECHANGES);
            }
        }
    }
}

