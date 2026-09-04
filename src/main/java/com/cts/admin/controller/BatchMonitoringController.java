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

        /* Load default tab */
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
            case BATCH_CAPTURE: loadBatches("Batch Capture Batches",  batchService.getBatchCaptureBatches()); break;
            case INWARD:        loadBatches("Inward Batches",          batchService.getInwardBatches());       break;
            case OUTWARD:       loadBatches("Outward Batches",         batchService.getOutwardBatches());      break;
        }
    }

    /* ------------------------------------------------------------------ */
    /* UNIFIED LOAD — same 10 columns for all three tabs                   */
    /* ------------------------------------------------------------------ */

    private void loadBatches(String titlePrefix, List<Batch> raw) {

        try {

            List<Batch> batches = filter(raw);

            batchSectionTitle.setValue(
                    titlePrefix + " (" + batches.size() + ")");

            batchListbox.getItems().clear();

            for (Batch b : batches) {

                Listitem item = new Listitem();

                /* 1. Batch ID */
                item.appendChild(buildIdCell(b.getBatchId()));

                /* 2. Branch */
                item.appendChild(buildTextCell(b.getBranch(), "batch-text-label"));

                /* 3. Cheque Count */
                item.appendChild(buildTextCell(
                        String.valueOf(b.getChequeCount()), "batch-cheques-label"));

                /* 4. Total Amount */
                item.appendChild(buildTextCell(
                        b.getTotalAmount() == 0 ? "-"
                                : String.format("%.2f", b.getTotalAmount()),
                        "batch-text-label"));

                /* 5. Current Module */
                item.appendChild(buildTextCell(b.getCurrentModule(), "batch-text-label"));

                /* 6. Status */
                item.appendChild(buildStatusCell(b.getStatus()));

                /* 7. Maker */
                item.appendChild(buildTextCell(b.getMaker(), "batch-user-label"));

                /* 8. Checker */
                item.appendChild(buildTextCell(b.getChecker(), "batch-user-label"));

                /* 9. Captured By */
                item.appendChild(buildTextCell(b.getCapturedBy(), "batch-user-label"));

                /* 10. Created At */
                item.appendChild(buildTextCell(
                        b.getCreatedAt() == null ? "-"
                                : DATE_FMT.format(b.getCreatedAt()),
                        "batch-date-label"));

                item.setValue(b);
                batchListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(
                    "Unable to load batch data.",
                    "Batch Monitoring",
                    Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    /* ------------------------------------------------------------------ */
    /* SEARCH FILTER                                                        */
    /* ------------------------------------------------------------------ */

    private List<Batch> filter(List<Batch> all) {

        String term = batchSearchTextbox.getValue();

        if (term == null || term.trim().isEmpty()) {
            return all;
        }

        String lower = term.trim().toLowerCase();
        List<Batch> result = new ArrayList<>();

        for (Batch b : all) {

            boolean match =
                    String.valueOf(b.getBatchId()).contains(lower)
                    || (b.getBranch()        != null && b.getBranch().toLowerCase().contains(lower))
                    || (b.getCurrentModule() != null && b.getCurrentModule().toLowerCase().contains(lower))
                    || (b.getMaker()         != null && b.getMaker().toLowerCase().contains(lower))
                    || (b.getChecker()       != null && b.getChecker().toLowerCase().contains(lower))
                    || (b.getCapturedBy()    != null && b.getCapturedBy().toLowerCase().contains(lower));

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
}
