package org.uberfire.client.editors.defaulteditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnSave;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.uberfire.client.workbench.type.AnyResourceType;

@Dependent
@WorkbenchEditor(identifier = "DefaultFileEditor", supportedTypes = { AnyResourceType.class }, priority = Integer.MIN_VALUE)
public class DefaultFileEditorPresenter {

    @Inject
    public TextEditorPresenter.View view;

    @Inject
    private Caller<VFSService> vfsServices;

    private Path path;

    @OnStartup
    public void onStartup( final Path path ) {
        this.path = path;
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String response ) {
                if ( response == null ) {
                    view.setContent( "-- empty --" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    @OnSave
    public void onSave() {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.setDirty( false );
            }
        } ).write( path, view.getContent() );
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnOpen
    public void onOpen() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Default Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}
