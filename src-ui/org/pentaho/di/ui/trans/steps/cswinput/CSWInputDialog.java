/**
 * 
 */
package org.pentaho.di.ui.trans.steps.cswinput;

import java.net.MalformedURLException;
import java.util.HashMap;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import org.eclipse.swt.widgets.Button;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.cswinput.CSWInputMeta;
import org.pentaho.di.trans.steps.cswinput.CSWReader;
import org.pentaho.di.trans.steps.cswinput.Messages;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;

/**
 * @author O.Mamadou
 *
 */
public class CSWInputDialog extends BaseStepDialog implements StepDialogInterface
{
	private CSWInputMeta input;
	private Group wGeneral;
	private Label wlUrl;
	private TextVar wUrl;
	private FormData fdUrl;
	private Button[] wMethod;
	private Label wlMethod;
	private FormData fdlMethod;
	private Label wlVersion;
	private FormData fdlVersion;
	private ComboVar wVersion;
	private FormData fdVersion;
	private FormData fdGeneral;
	private FormData fdlUrl;
	
	private ComboVar wMethodCSW;
	private FormData fdwMethod;
	
	private TextVar wGetCapabilitiesDoc;
	
	
	private Group wRequestGroup;
	private FormData fdRequestGroup;
	
	private TextVar wReqText;
	private FormData fdReqText;
	private Label wlReqLabel;
	private FormData fdReqTextLabel;
	
	private Button wGetCapabilitiesButton;
	private FormData fdGetCapabilitiesButton;
	private Label wlOutputSchemaLabel;
	private FormData fdwlOutputSchemaLabel;
	private ComboVar wOutputSchemaLabel;
	private FormData fdwOutputSchemaLabel;
	private Listener lsGetCapabilities;
	private Group wLoginGroup;
	private TextVar wUser;
	private FormData fdwUser;
	private FormData fdLoginGroup;
	private TextVar wPassword;
	private FormData fdwPassword;
	private Label wlUser;
	private FormData fdwlUser;
	private Label wlPassword;
	private FormData fdwlPassword;
	private TextVar wLoginUrl;
	private FormData fdLoginUrl;
	private Label wlLoginURL;
	private FormData fdlwlLoginURL;
	private Group wOutputGroup;
	private FormData fdOutputGroup;
	private Button wChkAdvanced;
	private FormData fdChkAdvanced;
	private Group wAdvancedGroup;
	private FormData fdAvancedGroup;
	private TextVar wTitle;
	private FormData fdTitle;
	private Label wlTitle;
	private FormData fdwlTitle;
	private Label wlDateDeb;
	private FormData fdwlDateDeb;
	private TextVar wDateDeb;
	private FormData fdwDateDeb;
	private Label wlDateFin;
	private FormData fdwlDateFin;
	private TextVar wDateFin;
	private FormData fdwDateFin;
	private Group wSpatialGroup;
	private FormData fdSpatialGroup;
	private TextVar wBoxNorth;
	private FormData fdwBoxNorth;
	private TextVar wBoxWest;
	private FormData fdwBoxWest;
	private TextVar wBoxEast;
	private FormData fdwBoxEast;
	private TextVar wBoxSouth;
	private FormData fdwBoxSouth;
	private Button wGetRecordButton;
	private FormData fdwGetRecordButton;
	private Button wOptResultType;
	private FormData fdwOptResultType;
	private Button wOptResultTypeBrief;
	private FormData fdwOptResultTypeBrief;
	private Button wOptResultTypeFull;
	private FormData fdwOptResultTypeFull;
	private Group ElementSetGroup;
	private FormData fdElementSetGroup;
	private CSWReader cswParam;
	
	
	public void setMethod(){
		if (wMethod[0].getSelection() || wMethod[1].getSelection()){
			if (wMethod[0].getSelection()) {
			}
			if (wMethod[1].getSelection()) {
			}
		} else {
		}		
	}

	public CSWInputDialog(Shell parent, Object in, TransMeta tr, String sname)
	{
		super(parent, (BaseStepMeta)in, tr, sname);
		input=(CSWInputMeta)in;
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
 		props.setLook(shell);
 		setShellImage(shell, input);
        
		ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				input.setChanged();
			}
		};
		changed = input.hasChanged();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("CSWInputDialog.Shell.Title")); //$NON-NLS-1$
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname=new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("CSWInputDialog.Stepname.Label")); //$NON-NLS-1$
 		props.setLook(wlStepname);
		fdlStepname=new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right= new FormAttachment(middle, -margin);
		fdlStepname.top  = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
 		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname=new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top  = new FormAttachment(0, margin);
		fdStepname.right= new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		
		
		wGeneral = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(wGeneral);
		wGeneral.setText(Messages.getString("CSWInputDialog.General.Tab"));
		FormLayout GeneralgroupLayout = new FormLayout();
		GeneralgroupLayout.marginWidth = 10;
		GeneralgroupLayout.marginHeight = 10;
		wGeneral.setLayout(GeneralgroupLayout);
		
		wlUrl=new Label(wGeneral, SWT.LEFT);
		wlUrl.setText(Messages.getString("CSWInputDialog.URL.Label")); //$NON-NLS-1$
 		props.setLook(wlUrl);
 		fdlUrl=new FormData();
		fdlUrl.left = new FormAttachment(0, 0);
		fdlUrl.top  = new FormAttachment(wStepname, 0);			
		wlUrl.setLayoutData(fdlUrl);
		
 		wUrl=new TextVar(transMeta, wGeneral, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wUrl);
		wUrl.addModifyListener(lsMod);
		fdUrl=new FormData();
		fdUrl.left = new FormAttachment(wlUrl, margin);
		fdUrl.top  = new FormAttachment(wStepname, margin);
		fdUrl.right= new FormAttachment(80, -1*margin);
		wUrl.setLayoutData(fdUrl);
		
		///
		wlLoginURL=new Label(wGeneral, SWT.LEFT);
		wlLoginURL.setText(Messages.getString("CSWInputDialog.LoginURL.Label"));
        props.setLook(wlLoginURL);
        fdlwlLoginURL=new FormData();
        fdlwlLoginURL.left = new FormAttachment(0, margin);
        fdlwlLoginURL.top  = new FormAttachment(wUrl, margin*3);        
        wlLoginURL.setLayoutData(fdlwlLoginURL);
		
		
		wLoginUrl=new TextVar(transMeta, wGeneral, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wLoginUrl);
 		
		fdLoginUrl=new FormData();
		fdLoginUrl.left = new FormAttachment(wlLoginURL, margin);
		fdLoginUrl.top  = new FormAttachment(wUrl, margin*3);
		fdLoginUrl.right= new FormAttachment(100, -1*margin);
		wLoginUrl.setLayoutData(fdLoginUrl); 
		

		//method
		
		 wMethodCSW=new ComboVar(transMeta, wGeneral, SWT.BORDER | SWT.READ_ONLY);
        wMethodCSW.setEditable(false);
        props.setLook(wMethodCSW);
	        
		
		
		//wMethod[1].addListener(SWT.Selection, lsMethod);
		
		wlMethod=new Label(wGeneral, SWT.LEFT);
        wlMethod.setText(Messages.getString("CSWInputDialog.Method.Label"));
        props.setLook(wlMethod);
        fdlMethod=new FormData();
        fdlMethod.left = new FormAttachment(0, margin);
        fdlMethod.top  = new FormAttachment(wLoginUrl, margin*3);        
        wlMethod.setLayoutData(fdlMethod);
        
        fdwMethod=new FormData();
        fdwMethod.left = new FormAttachment(wlMethod, margin);
        fdwMethod.top  = new FormAttachment(wLoginUrl, margin*3);
        fdwMethod.right= new FormAttachment(50, -1*margin);
        wMethodCSW.setLayoutData(fdwMethod);
        wMethodCSW.add("POST");
        wMethodCSW.add("GET");
        wMethodCSW.add("SOAP");
        
       

        //Version
        wlVersion=new Label(wGeneral, SWT.LEFT);
        wlVersion.setText(Messages.getString("CSWInputDialog.Version.Label"));
        props.setLook(wlVersion);
        fdlVersion=new FormData();
        fdlVersion.left = new FormAttachment(wMethodCSW, margin);
        fdlVersion.top  = new FormAttachment(wLoginUrl, margin*2);
        //fdlVersion.right= new FormAttachment(middle, -2*margin);
        wlVersion.setLayoutData(fdlVersion);
        wVersion=new ComboVar(transMeta, wGeneral, SWT.BORDER | SWT.READ_ONLY);
        wVersion.setEditable(false);
        props.setLook(wVersion);
        fdVersion=new FormData();
        fdVersion.left = new FormAttachment(wlVersion, 3*margin);
        fdVersion.top  = new FormAttachment(wLoginUrl, margin*2);
        fdVersion.right= new FormAttachment(100, -1*margin);
        wVersion.setLayoutData(fdVersion);
        wVersion.add("1.0.0");
        wVersion.addModifyListener(lsMod);
        
        /**
         * Login parameters
         * **/
        wLoginGroup = new Group(wGeneral, SWT.SHADOW_NONE);
		props.setLook(wLoginGroup);
		wLoginGroup.setText(Messages.getString("CSWInputDialog.Login.Group"));
		FormLayout LoginGroupLayout = new FormLayout();
		LoginGroupLayout.marginWidth = 10;
		LoginGroupLayout.marginHeight = 10;
		wLoginGroup.setLayout(LoginGroupLayout);
		
		fdLoginGroup=new FormData();
		fdLoginGroup.left = new FormAttachment(0, margin);
		fdLoginGroup.top  = new FormAttachment(wVersion, 3*margin);
		fdLoginGroup.right= new FormAttachment(100, -1*margin);
		wLoginGroup.setLayoutData(fdLoginGroup); 
		
		wlUser= new Label(wLoginGroup, SWT.LEFT);
		wlUser.setText(Messages.getString("CSWInputDialog.Username.Label"));
 		props.setLook(wlUser);
 		
		fdwlUser=new FormData();
		fdwlUser.left = new FormAttachment(0, margin);
		fdwlUser.top  = new FormAttachment(0, margin);		
		wlUser.setLayoutData(fdwlUser); 
		
		wUser=new TextVar(transMeta, wLoginGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wUser);
 		wUser.addModifyListener(lsMod);
		fdwUser=new FormData();
		fdwUser.left = new FormAttachment(wlUser, margin);
		fdwUser.top  = new FormAttachment(0, margin);
		fdwUser.right= new FormAttachment(100, -1*margin);
		wUser.setLayoutData(fdwUser); 
		
		wlPassword= new Label(wLoginGroup, SWT.LEFT);
		wlPassword.setText(Messages.getString("CSWInputDialog.Password.Label"));
 		props.setLook(wlPassword);
 		
		fdwlPassword=new FormData();
		fdwlPassword.left = new FormAttachment(0, margin);
		fdwlPassword.top  = new FormAttachment(wUser, margin);		
		wlPassword.setLayoutData(fdwlPassword); 
		
		wPassword=new TextVar(transMeta, wLoginGroup, SWT.SINGLE | SWT.PASSWORD |SWT.LEFT | SWT.BORDER);
 		props.setLook(wPassword);
 		wPassword.addModifyListener(lsMod);
		fdwPassword=new FormData();
		fdwPassword.left = new FormAttachment(wlPassword, margin);
		fdwPassword.top  = new FormAttachment(wUser, margin);
		fdwPassword.right= new FormAttachment(100, -1*margin);
		wPassword.setLayoutData(fdwPassword); 
		
		
        
        /**getCapabilities button
         * 
         * */
        wGetCapabilitiesButton=new Button(wGeneral, SWT.PUSH);
        wGetCapabilitiesButton.setText(Messages.getString("CSWInputDialog.Button.GetCapabilities"));
        lsGetCapabilities = new Listener()  {public void handleEvent(Event e){getCapabilities();}

		private void getCapabilities() {
			cswParam=new CSWReader();
			cswParam.setVersion(wVersion.getText());
			cswParam.setMethod(wMethodCSW.getText());
			try {
				cswParam.setCatalogUrl(wUrl.getText());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				String output=cswParam.GetCapabilities();
				System.out.println(output);
			} catch (KettleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		};
		wGetCapabilitiesButton.addListener(SWT.Selection, lsGetCapabilities);

        
        fdGetCapabilitiesButton = new FormData();
        fdGetCapabilitiesButton.left = new FormAttachment(wUrl, 3*margin);
        fdGetCapabilitiesButton.top = new FormAttachment(wStepname, margin);
        //fdGetCapabilitiesButton.right = new FormAttachment(70, -margin);
        wGetCapabilitiesButton.setLayoutData(fdGetCapabilitiesButton);
 
		fdGeneral = new FormData();
		fdGeneral.left  = new FormAttachment(0, margin);
		fdGeneral.top   = new FormAttachment(wStepname, margin);
		fdGeneral.right = new FormAttachment(100, -margin);
		wGeneral.setLayoutData(fdGeneral);
		
		
		/**
		 * 
		 * */
		wRequestGroup = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(wRequestGroup);
		wRequestGroup.setText(Messages.getString("CSWInputDialog.Query.Tab"));
		FormLayout RequestGroupLayout = new FormLayout();
		RequestGroupLayout.marginWidth = 10;
		RequestGroupLayout.marginHeight = 10;
		wRequestGroup.setLayout(RequestGroupLayout);
		
		fdRequestGroup = new FormData();
		fdRequestGroup.left  = new FormAttachment(0, margin);
		fdRequestGroup.top   = new FormAttachment(wGeneral, 1*margin);
		fdRequestGroup.right = new FormAttachment(100, -margin);
		wRequestGroup.setLayoutData(fdRequestGroup);
		
		
		
 		
 		
		
		/***Request Text and Label
		 * */
		
		wlReqLabel=new Label(wRequestGroup, SWT.LEFT);
		wlReqLabel.setText(Messages.getString("CSWInputDialog.Request.Label"));
		props.setLook(wlReqLabel);
		fdReqTextLabel=new FormData();
		fdReqTextLabel.left = new FormAttachment(0, 0);
		fdReqTextLabel.top  = new FormAttachment(0, margin);		
		wlReqLabel.setLayoutData(fdReqTextLabel);
 		
		wReqText=new TextVar(transMeta, wRequestGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wReqText);
 		fdReqText=new FormData();
 		fdReqText.left = new FormAttachment(wlReqLabel, margin);
 		fdReqText.top  = new FormAttachment(0, margin);
 		fdReqText.right= new FormAttachment(100, -40*margin);
 		wReqText.setLayoutData(fdReqText);
 		
 		/**
 		 * getRecord button
 		 * **/
 		wGetRecordButton=new Button(wRequestGroup, SWT.PUSH);
 		wGetRecordButton.setText(Messages.getString("CSWInputDialog.Button.GetRecord"));
        lsGetCapabilities = new Listener()  {public void handleEvent(Event e){setObservedProperties();}

		private void setObservedProperties() {
			// TODO Auto-generated method stub
			
			
			
		}	};
		wGetRecordButton.addListener(SWT.Selection, lsGetCapabilities);

        
        fdwGetRecordButton = new FormData();
        fdwGetRecordButton.left = new FormAttachment(wReqText, 3*margin);
        fdwGetRecordButton.top = new FormAttachment(0, margin);
        fdwGetRecordButton.right = new FormAttachment(100, -margin);
        wGetRecordButton.setLayoutData(fdwGetRecordButton);
        //
 		
 		//checkbox advanced query
 		wChkAdvanced= new Button(wRequestGroup, SWT.CHECK);
 		props.setLook(wChkAdvanced);
 		wChkAdvanced.setText("Advanced search");
 		fdChkAdvanced=new FormData();
 		fdChkAdvanced.left = new FormAttachment(0, margin);
 		fdChkAdvanced.top  = new FormAttachment(wReqText, 3*margin);
 		fdChkAdvanced.right= new FormAttachment(100, -1*margin);
 		wChkAdvanced.setLayoutData(fdChkAdvanced);
 		//listener
 		wChkAdvanced.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
            	input.setChanged();
            	if (wChkAdvanced.getSelection()==true)
            		wAdvancedGroup.setEnabled(true);
            	else
            		wAdvancedGroup.setEnabled(false);
            }
        }
 		);
 		
 		/**
 		 * advanced query group
 		 * **/
 		wAdvancedGroup = new Group(wRequestGroup, SWT.SHADOW_NONE);
 		wAdvancedGroup.setEnabled(false);
 		props.setLook(wAdvancedGroup);
 		wAdvancedGroup.setText(Messages.getString("CSWInputDialog.AdvancedQuery.Tab"));
		FormLayout AdvancedGroupLayout = new FormLayout();
		AdvancedGroupLayout.marginWidth = 10;
		AdvancedGroupLayout.marginHeight = 10;
		wAdvancedGroup.setLayout(AdvancedGroupLayout);
		
		fdAvancedGroup = new FormData();
		fdAvancedGroup.left  = new FormAttachment(0, margin);
		fdAvancedGroup.top   = new FormAttachment(wChkAdvanced, 4*margin);
		fdAvancedGroup.right = new FormAttachment(100, -margin);
		wAdvancedGroup.setLayoutData(fdAvancedGroup);
 		
		//title
		wlTitle=new Label(wAdvancedGroup, SWT.LEFT); 
		wlTitle.setText(Messages.getString("CSWInputDialog.AdvancedGroup.Title"));
		props.setLook(wlTitle);
 		fdwlTitle=new FormData();
 		fdwlTitle.left = new FormAttachment(0, margin);
 		fdwlTitle.top  = new FormAttachment(0, margin); 		
 		wlTitle.setLayoutData(fdwlTitle);
 		
		wTitle=new TextVar(transMeta, wAdvancedGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wTitle);
 		fdTitle=new FormData();
 		fdTitle.left = new FormAttachment(wlTitle, margin);
 		fdTitle.top  = new FormAttachment(0, margin);
 		fdTitle.right= new FormAttachment(100, -1*margin);
 		wTitle.setLayoutData(fdTitle);
		
 		//date deb et date fin
 		wlDateDeb=new Label(wAdvancedGroup, SWT.LEFT); 
 		wlDateDeb.setText(Messages.getString("CSWInputDialog.AdvancedGroup.DateDeb"));
		props.setLook(wlDateDeb);
 		fdwlDateDeb=new FormData();
 		fdwlDateDeb.left = new FormAttachment(0, margin);
 		fdwlDateDeb.top  = new FormAttachment(wTitle, margin); 		
 		wlDateDeb.setLayoutData(fdwlDateDeb);
 		
		wDateDeb=new TextVar(transMeta, wAdvancedGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wDateDeb);
 		fdwDateDeb=new FormData();
 		fdwDateDeb.left = new FormAttachment(wlDateDeb, margin);
 		fdwDateDeb.top  = new FormAttachment(wTitle, margin);
 		fdwDateDeb.right= new FormAttachment(50, -1*margin);
 		wDateDeb.setLayoutData(fdwDateDeb);
 		
 		//
 		wlDateFin=new Label(wAdvancedGroup, SWT.LEFT); 
 		wlDateFin.setText(Messages.getString("CSWInputDialog.AdvancedGroup.DateFin"));
		props.setLook(wlDateFin);
 		fdwlDateFin=new FormData();
 		fdwlDateFin.left = new FormAttachment(wDateDeb, 2*margin);
 		fdwlDateFin.top  = new FormAttachment(wTitle, margin); 		
 		wlDateFin.setLayoutData(fdwlDateFin);
 		
 		wDateFin=new TextVar(transMeta, wAdvancedGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wDateFin);
 		fdwDateFin=new FormData();
 		fdwDateFin.left = new FormAttachment(wlDateFin, margin);
 		fdwDateFin.top  = new FormAttachment(wTitle, margin);
 		fdwDateFin.right= new FormAttachment(100, -1*margin);
 		wDateFin.setLayoutData(fdwDateFin);
 		
 		/**
 		 * spatial search
 		 * **/
 		wSpatialGroup = new Group(wAdvancedGroup, SWT.SHADOW_NONE);
 		props.setLook(wSpatialGroup);
 		wSpatialGroup.setText(Messages.getString("CSWInputDialog.SpatialSearchGroup.Title"));
		FormLayout SpatialGroupLayout = new FormLayout();
		SpatialGroupLayout.marginWidth = 10;
		SpatialGroupLayout.marginHeight = 10;
		wSpatialGroup.setLayout(SpatialGroupLayout);
		
		fdSpatialGroup = new FormData();
		fdSpatialGroup.left  = new FormAttachment(wlDateDeb, 5*margin);
		fdSpatialGroup.top   = new FormAttachment(wDateFin, 3*margin);
		fdAvancedGroup.right = new FormAttachment(100, -margin);
		wSpatialGroup.setLayoutData(fdSpatialGroup);
		
		wBoxNorth=new TextVar(transMeta, wSpatialGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wBoxNorth);
 		fdwBoxNorth=new FormData();
 		fdwBoxNorth.left = new FormAttachment(middle, margin);
 		fdwBoxNorth.top  = new FormAttachment(0, margin);
 		fdwBoxNorth.right= new FormAttachment(100, -50*margin);
 		wBoxNorth.setLayoutData(fdwBoxNorth);
 		
 		wBoxWest=new TextVar(transMeta, wSpatialGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wBoxWest);
 		fdwBoxWest=new FormData();
 		fdwBoxWest.left = new FormAttachment(0, margin);
 		fdwBoxWest.top  = new FormAttachment(wBoxNorth, margin);
 		fdwBoxWest.right= new FormAttachment(100, -80*margin);
 		wBoxWest.setLayoutData(fdwBoxWest);
 		
 		wBoxEast=new TextVar(transMeta, wSpatialGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wBoxEast);
 		fdwBoxEast=new FormData();
 		fdwBoxEast.left = new FormAttachment(wBoxNorth, margin);
 		fdwBoxEast.top  = new FormAttachment(wBoxNorth, margin);
 		fdwBoxEast.right= new FormAttachment(100, -20*margin);
 		wBoxEast.setLayoutData(fdwBoxEast);
 		
 		wBoxSouth=new TextVar(transMeta, wSpatialGroup, SWT.BORDER | SWT.SINGLE); 
		props.setLook(wBoxSouth);
 		fdwBoxSouth=new FormData();
 		fdwBoxSouth.left = new FormAttachment(middle, margin);
 		fdwBoxSouth.top  = new FormAttachment(wBoxEast, margin);
 		fdwBoxSouth.right= new FormAttachment(100, -50*margin);
 		wBoxSouth.setLayoutData(fdwBoxSouth);
 		
 		//outputSchema and resultType (brief,Summary,full)
 		
 		wOutputGroup = new Group(wRequestGroup, SWT.SHADOW_NONE);
 		props.setLook(wOutputGroup);
 		wOutputGroup.setText(Messages.getString("CSWInputDialog.Output.Group"));
		FormLayout OutputGroupLayout = new FormLayout();
		OutputGroupLayout.marginWidth = 10;
		OutputGroupLayout.marginHeight = 10;
		wOutputGroup.setLayout(OutputGroupLayout);
		
		fdOutputGroup = new FormData();
		fdOutputGroup.left  = new FormAttachment(0, margin);
		fdOutputGroup.top   = new FormAttachment(wAdvancedGroup, 4*margin);
		fdOutputGroup.right = new FormAttachment(100, -margin);
		wOutputGroup.setLayoutData(fdOutputGroup);
 		
 		
		
		ElementSetGroup = new Group(wOutputGroup, SWT.SHADOW_NONE);
 		props.setLook(ElementSetGroup);
 		ElementSetGroup.setText(Messages.getString("CSWInputDialog.ElementSet.Group"));
		FormLayout ElementSetGroupLayout = new FormLayout();
		ElementSetGroupLayout.marginWidth = 10;
		ElementSetGroupLayout.marginHeight = 10;
		ElementSetGroup.setLayout(ElementSetGroupLayout);
		
		fdElementSetGroup = new FormData();
		fdElementSetGroup.left  = new FormAttachment(0, margin);
		fdElementSetGroup.top   = new FormAttachment(0, 2*margin);
		fdElementSetGroup.right = new FormAttachment(100, -margin);
		ElementSetGroup.setLayoutData(fdElementSetGroup);
		
		///
		
 		
 		

 		//
 		wOptResultType= new Button(ElementSetGroup, SWT.RADIO);
 		props.setLook(wOptResultType);
 		wOptResultType.setText(Messages.getString("CSWInputDialog.ElementSet.Summary"));
 		fdwOptResultType=new FormData();
 		fdwOptResultType.left = new FormAttachment(0, 20*margin);
 		fdwOptResultType.top  = new FormAttachment(0, 2*margin); 		
 		wOptResultType.setLayoutData(fdwOptResultType);
 		
 		wOptResultTypeBrief= new Button(ElementSetGroup, SWT.RADIO);
 		props.setLook(wOptResultTypeBrief);
 		wOptResultTypeBrief.setText(Messages.getString("CSWInputDialog.ElementSet.Brief"));
 		fdwOptResultTypeBrief=new FormData();
 		fdwOptResultTypeBrief.left = new FormAttachment(wOptResultType, 20*margin);
 		fdwOptResultTypeBrief.top  = new FormAttachment(0, 2*margin);
 		
 		wOptResultTypeBrief.setLayoutData(fdwOptResultTypeBrief);
 		
 		wOptResultTypeFull= new Button(ElementSetGroup, SWT.RADIO);
 		props.setLook(wOptResultTypeFull);
 		wOptResultTypeFull.setText(Messages.getString("CSWInputDialog.ElementSet.Full"));
 		fdwOptResultTypeFull=new FormData();
 		fdwOptResultTypeFull.left = new FormAttachment(wOptResultTypeBrief, 20*margin);
 		fdwOptResultTypeFull.top  = new FormAttachment(0, 2*margin);
 		
 		wOptResultTypeFull.setLayoutData(fdwOptResultTypeFull);
 		
 		//
 		
 		
 		/**
 		 * output schema
 		 * **/
 		wlOutputSchemaLabel=new Label(wOutputGroup, SWT.LEFT);
 		wlOutputSchemaLabel.setText(Messages.getString("CSWInputDialog.OutputSchema.Label"));
		props.setLook(wlOutputSchemaLabel);
		fdwlOutputSchemaLabel=new FormData();
		fdwlOutputSchemaLabel.left = new FormAttachment(0, margin);
		fdwlOutputSchemaLabel.top  = new FormAttachment(ElementSetGroup, 3*margin);		
		wlOutputSchemaLabel.setLayoutData(fdwlOutputSchemaLabel);
 		
		wOutputSchemaLabel= new ComboVar(transMeta, wOutputGroup, SWT.BORDER | SWT.READ_ONLY);
 			 
		props.setLook(wOutputSchemaLabel);
 		fdwOutputSchemaLabel=new FormData();
 		fdwOutputSchemaLabel.left = new FormAttachment(wlOutputSchemaLabel, margin);
 		fdwOutputSchemaLabel.top  = new FormAttachment(ElementSetGroup, 3*margin);
 		fdwOutputSchemaLabel.right= new FormAttachment(100, -1*margin);
 		wOutputSchemaLabel.setLayoutData(fdwOutputSchemaLabel);
		
		////
		
		
		
		// Some buttons
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("System.Button.OK")); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("System.Button.Cancel")); //$NON-NLS-1$

		setButtonPositions(new Button[] { wOK, wCancel }, margin, wRequestGroup);

		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener    (SWT.Selection, lsOK    );
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener( lsDef );
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );


		// Set the shell size, based upon previous time...
		setSize();
		
		getData();
		input.setChanged(changed);
	
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}
	
	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */ 
	public void getData()
	{
		wStepname.selectAll();
		if (!Const.isEmpty(input.getCswParam().getKeyword()))      
			wReqText.setText(input.getCswParam().getKeyword());
		
		if (!Const.isEmpty(input.getCswParam().getVersion())){
			wVersion.setText(input.getCswParam().getVersion());
		}
		if (!Const.isEmpty(input.getCswParam().getCatalogUrl().toString())){
			wUrl.setText(input.getCswParam().getCatalogUrl().toString());
		}
		if (!Const.isEmpty(input.getCswParam().getMethod())){
			wMethodCSW.setText(input.getCswParam().getMethod());
		}
		if (!Const.isEmpty(input.getCswParam().getStartDate())){
			wDateDeb.setText(input.getCswParam().getStartDate());
		}
		if (!Const.isEmpty(input.getCswParam().getEndDate())){
			wDateFin.setText(input.getCswParam().getEndDate());
		}
		if (!Const.isEmpty(input.getCswParam().getUsername())){
			wUser.setText(input.getCswParam().getUsername());
		}
		if (!Const.isEmpty(input.getCswParam().getPassword())){
			wPassword.setText(input.getCswParam().getPassword());
		}
		if (!Const.isEmpty(input.getCswParam().getLoginServiceUrl())){
			wLoginUrl.setText(input.getCswParam().getLoginServiceUrl());
		}
		if (!Const.isEmpty(input.getCswParam().getOutputSchema())){
			wOutputSchemaLabel.setText(input.getCswParam().getOutputSchema());
		}
		if (!Const.isEmpty(input.getCswParam().getTitle())){
			wTitle.setText(input.getCswParam().getTitle());
		}
		
		wChkAdvanced.setSelection(input.getCswParam().isSimpleSearch());
		
			
		if (!Const.isEmpty(input.getCswParam().getElementSet())){
			String value=input.getCswParam().getElementSet();
			if (value.equalsIgnoreCase(wOptResultType.getText())){
				wOptResultType.setSelection(true);
			}else
			if (value.equalsIgnoreCase(wOptResultTypeBrief.getText())){
				wOptResultTypeBrief.setSelection(true);
			}else
			if (value.equalsIgnoreCase(wOptResultTypeFull.getText())){
				wOptResultTypeFull.setSelection(true);
			}
	
		}//end element set
		wBoxNorth.setText(input.getCswParam().getBBOX().get("NORTH").toString());
		wBoxSouth.setText(input.getCswParam().getBBOX().get("SOUTH").toString());
		wBoxEast.setText(input.getCswParam().getBBOX().get("EAST").toString());
		wBoxWest.setText(input.getCswParam().getBBOX().get("WEST").toString());
	
		
	}
	
	private void cancel()
	{
		stepname=null;
		input.setChanged(changed);
		
		dispose();
	}
	
	private void ok()
	{
		String elementSet;
		if (wOptResultType.getSelection()==true){
			elementSet=wOptResultType.getText();
		}else{
			if (wOptResultTypeBrief.getSelection()==true){
				elementSet=wOptResultTypeBrief.getText();
			}else
				elementSet=wOptResultTypeFull.getText();
		}
		
		cswParam=new CSWReader();
		cswParam.setVersion(wVersion.getText());
		cswParam.setMethod(wMethodCSW.getText());
		cswParam.setStartDate(wDateDeb.getText());
		cswParam.setEndDate(wDateFin.getText());
		cswParam.setSimpleSearch(wChkAdvanced.getSelection());
		cswParam.setKeyword(wReqText.getText());
		cswParam.setUsername(wUser.getText());
		cswParam.setPassword(wPassword.getText());
		cswParam.setLoginServiceUrl(wLoginUrl.getText());
		cswParam.setOutputSchema(wOutputSchemaLabel.getText());
		cswParam.setTitle(wTitle.getText());
		cswParam.setElementSet(elementSet);
		
		HashMap<String, Double> bbox=new HashMap<String, Double>();
		bbox.put("NORTH", Double.parseDouble(wBoxNorth.getText()));
		bbox.put("SOUTH", Double.parseDouble(wBoxSouth.getText()));
		bbox.put("EAST", Double.parseDouble(wBoxEast.getText()));
		bbox.put("WEST", Double.parseDouble(wBoxWest.getText()));
		cswParam.setBBOX(bbox);
		
		try {
			cswParam.setCatalogUrl(wUrl.getText());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		input.setCswParam(cswParam);
		
		if (Const.isEmpty(wStepname.getText())) return;
		stepname = wStepname.getText(); // return value
		
		
		//input.setKeyword(wReqText.getText());
		
		dispose();
	}
}