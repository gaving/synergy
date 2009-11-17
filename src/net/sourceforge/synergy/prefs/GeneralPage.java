package net.sourceforge.synergy.prefs;

import net.sourceforge.synergy.Constants;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

/**
 * For the general preferences page
 */

public class GeneralPage extends FieldEditorPreferencePage {
    public GeneralPage() {
        super(FieldEditorPreferencePage.GRID);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors() {

        StringFieldEditor elvinServer = new StringFieldEditor(
        		Constants.ELVIN_SERVER,
                "Elvin:",
                getFieldEditorParent());
        elvinServer.getTextControl(getFieldEditorParent());
        addField(elvinServer);
        
        StringFieldEditor mysqlServer = new StringFieldEditor(
        		Constants.MYSQL_SERVER,
                "Mysql:",
                getFieldEditorParent());
        mysqlServer.getTextControl(getFieldEditorParent());
        addField(mysqlServer);
        
        StringFieldEditor mysqlUsername = new StringFieldEditor(
        		Constants.MYSQL_USERNAME,
                "Username:",
                getFieldEditorParent());
        mysqlUsername.getTextControl(getFieldEditorParent());
        addField(mysqlUsername);
        
        StringFieldEditor mysqlPassword = new StringFieldEditor(
        		Constants.MYSQL_PASSWORD,
                "Password:",
                getFieldEditorParent());
        mysqlPassword.getTextControl(getFieldEditorParent());
        addField(mysqlPassword);
        
        StringFieldEditor mysqlDatabase = new StringFieldEditor(
        		Constants.MYSQL_DATABASE,
                "Database:",
                getFieldEditorParent());
        mysqlDatabase.getTextControl(getFieldEditorParent());
        addField(mysqlDatabase);
    }
    
}
