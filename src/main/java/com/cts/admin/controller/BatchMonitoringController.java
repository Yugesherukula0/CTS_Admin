package com.cts.admin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.cts.admin.model.Batch;
import com.cts.admin.service.BatchService;
import com.cts.admin.service.BatchServiceImpl;

public class BatchMonitoringController
        extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    /* ------------------------------------------------------------------ */
    /* ZUL COMPONENTS — auto-wired by id                                   */
    /* ------------------------------------------------------------------ */

    private Button  tabBatchCapture;
    private Button  tabInward;
    private Button  tabOutward;
    private Button  batchSearchButton;
    private Textbox batchSearchTextbox;
    private Label   batchSectionTitle;
    private Listbox batchListbox;

    /* ------------------------------------------------------------------ */
    /* SERVICE                                                             */
    /* ------------------------------------------------------------------ */

    private BatchService batchService;

    /* ------------------------------------------------------------------ */
    /* STATE                                                               */
    /* ------------------------------------------------------------------ */

    private enum Tab { BATCH_CAPTURE, INWARD, OUTWARD }

    private Tab activeTab = Tab.BATCH_CAPTURE;

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    /* ------------------------------------------------------------------ */
    /* LIFECYCLE                                                           */
    /* ------------------------------------------------------------------ */

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        System.out.println("BatchMonitoringController loaded.");

        batchService = new BatchServiceImpl();

        tabBatchCapture.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    @Override public void onEvent(Event e) throws Exception {
                        switchTab(Tab.BATCH_CAPTURE);
                    }
                });

        tabInward.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    @Override public void onEvent(Event e) throws Exception {
                        switchTab(Tab.INWARD);
                    }
                });

        tabOutward.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    @Override public void onEvent(Event e) throws Exception {
                        switchTab(Tab.OUTWARD);
                    }
                });

        batchSearchButton.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    @Override public void onEvent(Event e) throws Exception {
                        refreshActiveTab();
                    }
                });

        batchSearchTextbox.addEventListener(Events.ON_OK,
                new EventListener<Event>() {
                    @Override public void onEvent(Event e) throws Exception {
                        refreshActiveTab();
                    }
                });

        switchTab(Tab.BATCH_CAPTURE);
    }

    /* ------------------------------------------------------------------ */
    /* TAB SWITCHING                                                        */
    /* ------------------------------------------------------------------ */

    private void switchTab(Tab tab) {

        activeTab = tab;

        tabBatchCapture.setSclass(tab == Tab.BATCH_CAPTURE
                ? "batch-tab-btn batch-tab-active" : "batch-tab-btn");
        tabInward.setSclass(tab == Tab.INWARD
                ? "batch-tab-btn batch-tab-active" : "batch-tab-btn");
        tabOutward.setSclass(tab == Tab.OUTWARD
                ? "batch-tab-btn batch-tab-active" : "batch-tab-btn");

        refreshActiveTab();
    }

    private void refreshActiveTab() {
        switch (activeTab) {
            case BATCH_CAPTURE: loadBatchCapture(); break;
            case INWARD:        loadInward();       break;
            case OUTWARD:       loadOutward();      break;
        }
    }

    /* ------------------------------------------------------------------ */
    /* BATCH CAPTURE
     * Columns: Batch ID | Branch | Cheque Count | Created By | Created At
     * ------------------------------------------------------------------ */

    private void loadBatchCapture() {

        try {

            List<Batch> batches = filterCapture(batchService.getBatchCaptureBatches());

            batchSectionTitle.setValue("Batch Capture Batches (" + batches.size() + ")");

            rebuildListhead(
                    "Batch ID",    "12%",
                    "Branch",      "18%",
                    "Cheque Count","18%",
                    "Created By",  "22%",
                    "Created At",  "30%");

            batchListbox.getItems().clear();

            for (Batch b : batches) {

                Listitem item = new Listitem();

                item.appendChild(buildIdCell(b.getBatchId()));
                item.appendChild(buildTextCell(b.getBranch(),      "batch-text-label"));
                item.appendChild(buildTextCell(
                        String.valueOf(b.getChequeCount()),         "batch-cheques-label"));
                item.appendChild(buildTextCell(b.getCapturedBy(),  "batch-user-label"));
                item.appendChild(buildDateCell(b.getCreatedAt()));

                item.setValue(b);
                batchListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load Batch Capture data.");
        }
    }

    /* ------------------------------------------------------------------ */
    /* INWARD BATCHES
     * Columns: Batch ID | Cheque Count | Status | Maker | Checker |
     *          Maker Receive Time | Checker Receive Time | Batch Completion Time
     * ------------------------------------------------------------------ */

    private void loadInward() {

        try {

            List<Batch> batches = filterInwardOutward(batchService.getInwardBatches());

            batchSectionTitle.setValue("Inward Batches (" + batches.size() + ")");

            rebuildListhead(
                    "Batch ID",               "9%",
                    "Cheque Count",           "9%",
                    "Status",                 "10%",
                    "Maker",                  "10%",
                    "Checker",                "10%",
                    "Maker Receive Time",     "17%",
                    "Checker Receive Time",   "17%",
                    "Batch Completion Time",  "18%");

            batchListbox.getItems().clear();

            for (Batch b : batches) {

                Listitem item = new Listitem();

                item.appendChild(buildIdCell(b.getBatchId()));
                item.appendChild(buildTextCell(
                        String.valueOf(b.getChequeCount()),  "batch-cheques-label"));
                item.appendChild(buildStatusCell(b.getStatus()));
                item.appendChild(buildTextCell(b.getMaker(),   "batch-user-label"));
                item.appendChild(buildTextCell(b.getChecker(), "batch-user-label"));
                item.appendChild(buildDateCell(b.getMakerReceiveTime()));
                item.appendChild(buildDateCell(b.getCheckerReceiveTime()));
                item.appendChild(buildDateCell(b.getBatchCompletionTime()));

                item.setValue(b);
                batchListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load Inward Batch data.");
        }
    }

    /* ------------------------------------------------------------------ */
    /* OUTWARD BATCHES — same columns as Inward
     * ------------------------------------------------------------------ */

    private void loadOutward() {

        try {

            List<Batch> batches = filterInwardOutward(batchService.getOutwardBatches());

            batchSectionTitle.setValue("Outward Batches (" + batches.size() + ")");

            rebuildListhead(
                    "Batch ID",               "9%",
                    "Cheque Count",           "9%",
                    "Status",                 "10%",
                    "Maker",                  "10%",
                    "Checker",                "10%",
                    "Maker Receive Time",     "17%",
                    "Checker Receive Time",   "17%",
                    "Batch Completion Time",  "18%");

            batchListbox.getItems().clear();

            for (Batch b : batches) {

                Listitem item = new Listitem();

                item.appendChild(buildIdCell(b.getBatchId()));
                item.appendChild(buildTextCell(
                        String.valueOf(b.getChequeCount()),  "batch-cheques-label"));
                item.appendChild(buildStatusCell(b.getStatus()));
                item.appendChild(buildTextCell(b.getMaker(),   "batch-user-label"));
                item.appendChild(buildTextCell(b.getChecker(), "batch-user-label"));
                item.appendChild(buildDateCell(b.getMakerReceiveTime()));
                item.appendChild(buildDateCell(b.getCheckerReceiveTime()));
                item.appendChild(buildDateCell(b.getBatchCompletionTime()));

                item.setValue(b);
                batchListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load Outward Batch data.");
        }
    }

    /* ------------------------------------------------------------------ */
    /* LISTHEAD BUILDER
     * Tears out existing listhead and inserts new one with correct columns.
     * Accepts alternating label/width pairs.
     * ------------------------------------------------------------------ */

    private void rebuildListhead(String... labelWidthPairs) {

        Listhead existing = batchListbox.getListhead();
        if (existing != null) {
            existing.detach();
        }

        Listhead head = new Listhead();
        head.setSclass("batch-listhead");

        for (int i = 0; i < labelWidthPairs.length - 1; i += 2) {
            Listheader h = new Listheader(labelWidthPairs[i]);
            h.setWidth(labelWidthPairs[i + 1]);
            head.appendChild(h);
        }

        batchListbox.insertBefore(head, null);
    }

    /* ------------------------------------------------------------------ */
    /* SEARCH FILTERS                                                       */
    /* ------------------------------------------------------------------ */

    private List<Batch> filterCapture(List<Batch> all) {

        String term = batchSearchTextbox.getValue();
        if (term == null || term.trim().isEmpty()) return all;

        String lower = term.trim().toLowerCase();
        List<Batch> result = new ArrayList<>();

        for (Batch b : all) {
            boolean match =
                    String.valueOf(b.getBatchId()).contains(lower)
                    || (b.getBranch()     != null && b.getBranch().toLowerCase().contains(lower))
                    || (b.getCapturedBy() != null && b.getCapturedBy().toLowerCase().contains(lower));
            if (match) result.add(b);
        }
        return result;
    }

    private List<Batch> filterInwardOutward(List<Batch> all) {

        String term = batchSearchTextbox.getValue();
        if (term == null || term.trim().isEmpty()) return all;

        String lower = term.trim().toLowerCase();
        List<Batch> result = new ArrayList<>();

        for (Batch b : all) {
            boolean match =
                    String.valueOf(b.getBatchId()).contains(lower)
                    || (b.getMaker()   != null && b.getMaker().toLowerCase().contains(lower))
                    || (b.getChecker() != null && b.getChecker().toLowerCase().contains(lower))
                    || (b.getStatus()  != null && b.getStatus().toLowerCase().contains(lower));
            if (match) result.add(b);
        }
        return result;
    }

    /* ------------------------------------------------------------------ */
    /* CELL BUILDERS                                                       */
    /* ------------------------------------------------------------------ */

    private Listcell buildIdCell(Long batchId) {
        Listcell cell = new Listcell();
        Label label   = new Label(batchId == null ? "-" : String.valueOf(batchId));
        label.setSclass("batch-id-label");
        cell.appendChild(label);
        return cell;
    }

    private Listcell buildTextCell(String text, String sclass) {
        Listcell cell = new Listcell();
        Label label   = new Label(text == null || text.trim().isEmpty() ? "-" : text);
        label.setSclass(sclass);
        cell.appendChild(label);
        return cell;
    }

    private Listcell buildDateCell(java.sql.Timestamp ts) {
        Listcell cell = new Listcell();
        Label label   = new Label(ts == null ? "-" : DATE_FMT.format(ts));
        label.setSclass("batch-date-label");
        cell.appendChild(label);
        return cell;
    }

    private Listcell buildStatusCell(String status) {

        Listcell cell = new Listcell();

        if (status == null || status.trim().isEmpty()) {
            cell.appendChild(new Label("-"));
            return cell;
        }

        Hbox badge = new Hbox();
        badge.setAlign("center");

        String upper = status.trim().toUpperCase();

        if (upper.equals("PROCESSED") || upper.equals("COMPLETED") || upper.equals("ACTIVE")) {
            badge.setSclass("batch-status-badge batch-status-green");
        } else if (upper.equals("PENDING") || upper.equals("IN_PROGRESS")) {
            badge.setSclass("batch-status-badge batch-status-amber");
        } else if (upper.equals("FAILED") || upper.equals("REJECTED")) {
            badge.setSclass("batch-status-badge batch-status-red");
        } else {
            badge.setSclass("batch-status-badge batch-status-grey");
        }

        Label lbl = new Label(status);
        lbl.setSclass("batch-status-label");
        badge.appendChild(lbl);
        cell.appendChild(badge);

        return cell;
    }

    private void showError(String msg) {
        Messagebox.show(msg, "Batch Monitoring", Messagebox.OK, Messagebox.ERROR);
    }
}
