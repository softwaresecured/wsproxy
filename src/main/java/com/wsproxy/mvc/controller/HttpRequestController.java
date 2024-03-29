package com.wsproxy.mvc.controller;

import com.wsproxy.client.HttpClient;
import com.wsproxy.client.HttpClientException;
import com.wsproxy.httpproxy.HttpMessage;
import com.wsproxy.mvc.model.HttpRequestTesterModel;
import com.wsproxy.mvc.model.LogModel;
import com.wsproxy.mvc.view.frames.FrmHttpRequestTester;
import com.wsproxy.mvc.view.frames.FrmLogsView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class HttpRequestController implements PropertyChangeListener {

    private HttpRequestTesterModel httpRequestTesterModel;
    private FrmHttpRequestTester frmHttpRequestTester;
    private HttpClient httpClient = new HttpClient();

    public HttpRequestController(HttpRequestTesterModel httpRequestTesterModel, FrmHttpRequestTester frmHttpRequestTester) {
        this.httpRequestTesterModel = httpRequestTesterModel;
        this.frmHttpRequestTester = frmHttpRequestTester;
        this.httpRequestTesterModel.addListener(this);
        initEventListeners();
    }

    public void initEventListeners() {
        frmHttpRequestTester.pnlHttpRequestResponse.jbtnRunTest.addActionListener( actionEvent -> {
            httpRequestTesterModel.setErrorText(null);
            HttpMessage msg = null;
            if ( frmHttpRequestTester.pnlHttpRequestResponse.pnlUpgradeRequestScriptConfig.jchkUpgrade.isSelected() ) {
                String selectedScript = (String) frmHttpRequestTester.pnlHttpRequestResponse.pnlUpgradeRequestScriptConfig.jcmbUpgradeScripts.getSelectedItem();
                msg = frmHttpRequestTester.getHttpRequestResponseController().processUpgradeScript(selectedScript);
            }
            else {
                msg = frmHttpRequestTester.getHttpRequestResponseModel().buildHttpMessage();
            }

            if ( msg != null ) {
                try {
                    HttpMessage response = httpClient.send(msg);
                    if ( response != null ) {
                        frmHttpRequestTester.pnlHttpRequestResponse.jtxtHttpUpgradeMessageResponse.setText(new String(response.getBytes()));
                        frmHttpRequestTester.pnlHttpRequestResponse.getUpgradeHttpMsgTabs().setSelectedIndex(1);

                    }
                } catch (HttpClientException e) {
                    httpRequestTesterModel.setErrorText(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if ( "HttpRequestTesterModel.errorText".equals(propertyChangeEvent.getPropertyName())) {
            if ( propertyChangeEvent.getNewValue() != null ) {
                frmHttpRequestTester.jtxtErrors.setText((String)propertyChangeEvent.getNewValue());
                frmHttpRequestTester.pnlErrors.setVisible(true);
            }
            else {
                frmHttpRequestTester.jtxtErrors.setText("");
                frmHttpRequestTester.pnlErrors.setVisible(false);
            }
        }
    }
}
