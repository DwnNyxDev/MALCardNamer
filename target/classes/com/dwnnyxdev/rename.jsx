//start of data
//start of code
var docs = app.documents;
for(var group in groups){
    var users = groups[group].users;
    var psds = groups[group].psds;
    var edName = groups[group].editionName;

    for(var d=0; d<app.documents.length; d++){
        app.documents[d].close(SaveOptions.DONOTSAVECHANGES);
        d--;
    }

    for(var psd in psds){
        app.open(new File(psds[psd].path));
    }

    var ed_folder = Folder(save_location+"/"+edName);
    if(!ed_folder.exists){
        ed_folder.create();
    }

    for(var user in users){
        if(users[user].cards.length>0){
            var user_folder = Folder(save_location+"/"+edName+"/"+users[user].names[0]);
            if(!user_folder.exists){
                user_folder.create();
            }
            
            for(var u=0; u<users[user].cards.length; u++){
                var card = users[user].cards[u];
                var cLimit=20;
                var repString=true;
                var replaceWord="Name";
                var repLayer=false;
                var replaceLayer="Name";
                var saveAs="png";
                for(var psdFind in psds){
                    if(psds[psdFind].name==card){
                        cLimit=psds[psdFind].limit;
                        repString = psds[psdFind].repString;
                        replaceWord = psds[psdFind].replaceWord;
                        repLayer = psds[psdFind].repLayer;
                        replaceLayer = psds[psdFind].replaceLayer;
                        saveAs = psds[psdFind].saveAs;
                        break;
                    }
                }
                var user_name = users[user].names[0];
                if(user_name.length>cLimit&&users[user].names.length>1){
                    user_name=users[user].names[1];
                }
                var card_file = new File(user_folder+"/"+user_name+"_"+card.replace(".psd",""));
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
                            if(layer.kind == LayerKind.TEXT && !layer.allLocked){
                                if(repLayer){
                                    if(layer.name==replaceLayer){
                                        if(repString){
                                            layer.textItem.contents=layer.textItem.contents.replace(replaceWord,user_name);
                                        }
                                        else{
                                            layer.textItem.contents=user_name;
                                        } 
                                    }
                                }
                                else if(repString){
                                    layer.textItem.contents=layer.textItem.contents.replace(replaceWord,user_name);
                                } 
                            }
                        }
                    }
                    if(saveAs=="png"){
                        var pngOpts = new PNGSaveOptions;
                        app.activeDocument.saveAs(card_file,pngOpts,true,Extension.LOWERCASE);
                    }
                    else if(saveAs=="gif"){
                        var gifOpts = new GIFSaveOptions;
                        app.activeDocument.saveAs(card_file,gifOpts,true,Extension.LOWERCASE);
                    }
                    app.activeDocument.close(SaveOptions.DONOTSAVECHANGES);
                }
            }
        }
    }
}

