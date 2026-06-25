package com.auto_persona.pc;

import com.auto_persona.pc.model.*;
import com.auto_persona.pc.service.SaveService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class Main extends JFrame {

    private SaveData saveData;
    private final SaveService saveService = new SaveService();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JLabel statusLabel = new JLabel("未加载存档");
    private File currentFile;

    // UI field references
    private JTextField tfName, tfBirthday, tfIdentity, tfGender, tfCharName, tfCharPersonality, tfCharMood,
            tfPersonaPackId, tfCurrentOutfit, tfGameStartDate, tfDestName, tfCityName, tfCurrentCity,
            tfTravelState, tfTravelStart, tfTravelEnd, tfShopTab;
    private JSpinner spAffection, spTrust, spSystemAffection, spSystemTrust, spCoins, spQuestLevel,
            spGlobalLevel, spGlobalExp, spCurrentDay, spDailyActions, spActionsUsed, spAffectionCap,
            spTrustMin, spTrustMax, spShyness, spReminderCount;
    private JSlider slMusic, slSound, slBgm, slVoice;
    private JCheckBox cbCollected, cbDiaryLeak, cbYandere, cbGiftPref, cbAff30, cbAff70,
            cbDiaryRecovered, cbYandereRecovered, cbOutfit19, cbCatForm, cbAutoSave, cbMuted,
            cbTts, cbPreset, cbTravelMode, cbNightDialogue;
    private JSpinner[] spDateHistory = new JSpinner[7];
    private JSpinner[] spGifts = new JSpinner[12];
    private JCheckBox[] cbOutfits = new JCheckBox[20];
    private JTextArea taDescription, taPersonality, taScenario, taCreatorNotes, taFirstMes, taTags, taDiaryContent;
    private JComboBox<String> cmbPromptSelect;
    private JList<String> listFavoriteGifts, listSpecialEvents, listTimeEvents, listDiary;
    private DefaultListModel<String> giftsModel, eventsModel, timeEventsModel, diaryModel;
    private JSpinner spDiaryAffection;

    public Main() {
        super("auto_persona 存档编辑器 (PC版)");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (saveData != null) {
                    int r = JOptionPane.showConfirmDialog(Main.this, "有未保存的更改，是否退出？", "确认退出", JOptionPane.YES_NO_OPTION);
                    if (r != JOptionPane.YES_OPTION) return;
                }
                dispose();
            }
        });

        setSize(900, 700);
        setLocationRelativeTo(null);
        buildMenuBar();
        buildTabs();
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void buildMenuBar() {
        var menuBar = new JMenuBar();
        var fileMenu = new JMenu("文件");
        var miOpen = new JMenuItem("打开存档...");
        miOpen.addActionListener(e -> openFile());
        var miSave = new JMenuItem("保存");
        miSave.addActionListener(e -> saveFile());
        var miSaveAs = new JMenuItem("另存为...");
        miSaveAs.addActionListener(e -> saveFileAs());
        var miLoadTemplate = new JMenuItem("加载模板存档");
        miLoadTemplate.addActionListener(e -> loadTemplate());
        fileMenu.add(miOpen); fileMenu.add(miSave); fileMenu.add(miSaveAs);
        fileMenu.addSeparator(); fileMenu.add(miLoadTemplate);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void openFile() {
        var chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSON 文件", "json"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                currentFile = chooser.getSelectedFile();
                saveData = saveService.loadFromFile(currentFile);
                loadDataToUI();
                statusLabel.setText("已加载: " + currentFile.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "加载失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (saveData == null) { JOptionPane.showMessageDialog(this, "请先加载存档"); return; }
        saveDataFromUI();
        try {
            if (currentFile == null) { saveFileAs(); return; }
            saveService.saveToFile(currentFile, saveData);
            statusLabel.setText("已保存: " + currentFile.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFileAs() {
        if (saveData == null) { JOptionPane.showMessageDialog(this, "请先加载存档"); return; }
        var chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSON 文件", "json"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            if (!currentFile.getName().endsWith(".json")) currentFile = new File(currentFile.getPath() + ".json");
            saveFile();
        }
    }

    private void loadTemplate() {
        try {
            saveData = saveService.loadFromResource("存档.json");
            currentFile = null;
            loadDataToUI();
            statusLabel.setText("已加载模板存档");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载模板失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---- UI Building ----
    private void buildTabs() {
        tabbedPane.addTab("玩家信息", buildPlayerInfoPanel());
        tabbedPane.addTab("角色", buildCharacterPanel());
        tabbedPane.addTab("进度/时间", buildProgressPanel());
        tabbedPane.addTab("背包/商店", buildInventoryShopPanel());
        tabbedPane.addTab("约会/旅行", buildDateTimeTravelPanel());
        tabbedPane.addTab("设置", buildSettingsPanel());
        tabbedPane.addTab("日记", buildDiaryPanel());
        tabbedPane.addTab("Prompt", buildPromptPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JComponent buildPlayerInfoPanel() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("玩家信息"));
        var gbc = gbc();
        tfName = addField(p, "姓名", "", gbc, 0);
        tfBirthday = addField(p, "生日", "", gbc, 1);
        tfIdentity = addField(p, "身份", "", gbc, 2);
        tfGender = addField(p, "性别", "brother", gbc, 3);
        cbCollected = addCheck(p, "已领取", gbc, 4);
        tfCharName = addField(p, "角色名", "", gbc, 5);
        tfCharPersonality = addField(p, "角色性格", "", gbc, 6);
        tfCharMood = addField(p, "角色心情", "normal", gbc, 7);
        spAffection = addSpinner(p, "好感度", 0, 1000, gbc, 8);
        spTrust = addSpinner(p, "信任度", 0, 100, gbc, 9);
        spSystemAffection = addSpinner(p, "系统好感", 0, 1000, gbc, 10);
        spSystemTrust = addSpinner(p, "系统信任", 0, 100, gbc, 11);
        return p;
    }

    private JComponent buildCharacterPanel() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("角色系统"));
        var gbc = gbc();
        int row = 0;

        cbDiaryLeak = addCheck(p, "日记泄露已触发", gbc, row++);
        cbYandere = addCheck(p, "病娇事件已触发", gbc, row++);
        cbGiftPref = addCheck(p, "礼物偏好事件", gbc, row++);
        cbAff30 = addCheck(p, "好感30事件", gbc, row++);
        cbAff70 = addCheck(p, "好感70事件", gbc, row++);
        spAffectionCap = addSpinner(p, "好感度上限", 0, 10000, gbc, row++);
        spTrustMin = addSpinner(p, "信任度下限", -100, 100, gbc, row++);
        spTrustMax = addSpinner(p, "信任度上限", 0, 1000, gbc, row++);
        spShyness = addSpinner(p, "害羞值", 0, 100, gbc, row++);
        cbCatForm = addCheck(p, "猫形态", gbc, row++);
        tfPersonaPackId = addField(p, "当前人格包ID", "", gbc, row++);
        cbDiaryRecovered = addCheck(p, "日记泄露已恢复", gbc, row++);
        cbYandereRecovered = addCheck(p, "病娇已恢复", gbc, row++);
        cbOutfit19 = addCheck(p, "服装19已解锁", gbc, row++);
        tfCurrentOutfit = addField(p, "当前服装", "", gbc, row++);
        spReminderCount = addSpinner(p, "兽耳提醒次数", 0, 100, gbc, row++);

        row++;
        var giftPanel = new JPanel(new BorderLayout());
        giftPanel.setBorder(new TitledBorder("喜好礼物"));
        giftsModel = new DefaultListModel<>();
        listFavoriteGifts = new JList<>(giftsModel);
        giftPanel.add(new JScrollPane(listFavoriteGifts), BorderLayout.CENTER);
        var giftBtnPanel = new JPanel();
        var tfGift = new JTextField(10);
        var btnAddGift = new JButton("+");
        btnAddGift.addActionListener(e -> { if (!tfGift.getText().isBlank()) { giftsModel.addElement(tfGift.getText().trim()); tfGift.setText(""); } });
        var btnDelGift = new JButton("-");
        btnDelGift.addActionListener(e -> { if (listFavoriteGifts.getSelectedIndex() >= 0) giftsModel.remove(listFavoriteGifts.getSelectedIndex()); });
        giftBtnPanel.add(tfGift); giftBtnPanel.add(btnAddGift); giftBtnPanel.add(btnDelGift);
        giftPanel.add(giftBtnPanel, BorderLayout.SOUTH);
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1; p.add(giftPanel, gbc);

        var eventPanel = new JPanel(new BorderLayout());
        eventPanel.setBorder(new TitledBorder("特殊事件"));
        eventsModel = new DefaultListModel<>();
        listSpecialEvents = new JList<>(eventsModel);
        eventPanel.add(new JScrollPane(listSpecialEvents), BorderLayout.CENTER);
        var evtBtnPanel = new JPanel();
        var tfEvent = new JTextField(10);
        var btnAddEvt = new JButton("+");
        btnAddEvt.addActionListener(e -> { if (!tfEvent.getText().isBlank()) { eventsModel.addElement(tfEvent.getText().trim()); tfEvent.setText(""); } });
        var btnDelEvt = new JButton("-");
        btnDelEvt.addActionListener(e -> { if (listSpecialEvents.getSelectedIndex() >= 0) eventsModel.remove(listSpecialEvents.getSelectedIndex()); });
        evtBtnPanel.add(tfEvent); evtBtnPanel.add(btnAddEvt); evtBtnPanel.add(btnDelEvt);
        eventPanel.add(evtBtnPanel, BorderLayout.SOUTH);
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2; p.add(eventPanel, gbc);

        return new JScrollPane(p);
    }

    private JComponent buildProgressPanel() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("进度与时间"));
        var gbc = gbc();
        int row = 0;
        spCoins = addSpinner(p, "金币", 0, Integer.MAX_VALUE, gbc, row++);
        spQuestLevel = addSpinner(p, "任务等级", 0, 100, gbc, row++);
        spGlobalLevel = addSpinner(p, "全局等级", 1, 1000, gbc, row++);
        spGlobalExp = addSpinner(p, "全局经验", 0, Integer.MAX_VALUE, gbc, row++);
        spCurrentDay = addSpinner(p, "当前天数", 1, 9999, gbc, row++);
        spDailyActions = addSpinner(p, "每日行动次数", 0, Integer.MAX_VALUE, gbc, row++);
        spActionsUsed = addSpinner(p, "已用行动", 0, Integer.MAX_VALUE, gbc, row++);
        tfGameStartDate = addField(p, "游戏开始日期", "", gbc, row++);

        var timeEventPanel = new JPanel(new BorderLayout());
        timeEventPanel.setBorder(new TitledBorder("时间特殊事件"));
        timeEventsModel = new DefaultListModel<>();
        listTimeEvents = new JList<>(timeEventsModel);
        timeEventPanel.add(new JScrollPane(listTimeEvents), BorderLayout.CENTER);
        var btnP = new JPanel();
        var tfTE = new JTextField(10);
        var btnAdd = new JButton("+"); btnAdd.addActionListener(e -> { if (!tfTE.getText().isBlank()) { timeEventsModel.addElement(tfTE.getText().trim()); tfTE.setText(""); } });
        var btnDel = new JButton("-"); btnDel.addActionListener(e -> { if (listTimeEvents.getSelectedIndex() >= 0) timeEventsModel.remove(listTimeEvents.getSelectedIndex()); });
        btnP.add(tfTE); btnP.add(btnAdd); btnP.add(btnDel);
        timeEventPanel.add(btnP, BorderLayout.SOUTH);
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1; p.add(timeEventPanel, gbc);

        return new JScrollPane(p);
    }

    private JComponent buildInventoryShopPanel() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("商店"));
        var gbc = gbc();
        int row = 0;

        var outfitPanel = new JPanel(new GridLayout(4, 5, 4, 4));
        outfitPanel.setBorder(new TitledBorder("服装"));
        for (int i = 0; i < 20; i++) { cbOutfits[i] = new JCheckBox("" + (i + 1)); outfitPanel.add(cbOutfits[i]); }
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2; p.add(outfitPanel, gbc);

        var giftInvPanel = new JPanel(new GridLayout(4, 3, 4, 4));
        giftInvPanel.setBorder(new TitledBorder("礼物库存"));
        for (int i = 0; i < 12; i++) {
            var sub = new JPanel(new FlowLayout());
            sub.add(new JLabel("礼物" + (i + 1) + ":"));
            spGifts[i] = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
            sub.add(spGifts[i]);
            giftInvPanel.add(sub);
        }
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2; p.add(giftInvPanel, gbc);

        tfShopTab = addField(p, "商店标签页", "travel", gbc, row++);
        return new JScrollPane(p);
    }

    private JComponent buildDateTimeTravelPanel() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("约会和旅行"));
        var gbc = gbc();
        int row = 0;
        String[] dateLabels = {"自定义", "咖啡厅", "公园", "餐厅", "海滩", "夜市", "夜路"};
        for (int i = 0; i < 7; i++) spDateHistory[i] = addSpinner(p, dateLabels[i] + "约会", 0, 9999, gbc, row++);

        row++;
        cbTravelMode = addCheck(p, "旅行模式", gbc, row++);
        cbNightDialogue = addCheck(p, "已显示夜间对话", gbc, row++);
        tfDestName = addField(p, "当前目的地", "", gbc, row++);
        tfCityName = addField(p, "目的地城市名", "", gbc, row++);
        tfCurrentCity = addField(p, "当前城市", "", gbc, row++);
        tfTravelState = addField(p, "旅行状态", "", gbc, row++);
        tfTravelStart = addField(p, "旅行开始", "", gbc, row++);
        tfTravelEnd = addField(p, "旅行结束", "", gbc, row++);

        return new JScrollPane(p);
    }

    private JPanel buildSettingsPanel() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("设置"));
        var gbc = gbc();
        int row = 0;
        slMusic = addSlider(p, "音乐音量", 0, 100, 70, gbc, row++);
        slSound = addSlider(p, "音效音量", 0, 100, 80, gbc, row++);
        cbAutoSave = addCheck(p, "自动存档", gbc, row++);
        slBgm = addSlider(p, "BGM音量", 0, 100, 70, gbc, row++);
        slVoice = addSlider(p, "语音音量", 0, 100, 80, gbc, row++);
        cbMuted = addCheck(p, "静音", gbc, row++);
        cbTts = addCheck(p, "TTS已启用", gbc, row++);
        cbPreset = addCheck(p, "预设回复", gbc, row++);
        return p;
    }

    private JPanel buildDiaryPanel() {
        var p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new TitledBorder("日记"));
        diaryModel = new DefaultListModel<>();
        listDiary = new JList<>(diaryModel);
        p.add(new JScrollPane(listDiary), BorderLayout.CENTER);

        var editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(new TitledBorder("编辑条目"));
        var gbc = gbc(); int row = 0;
        var tfDate = new JTextField(10);
        var tfTime = new JTextField(10);
        var tfMode = new JTextField(10);
        spDiaryAffection = new JSpinner(new SpinnerNumberModel(0, -999, 9999, 1));
        taDiaryContent = new JTextArea(5, 20);
        editPanel.add(new JLabel("日期:"), gbc0(row, 0)); editPanel.add(tfDate, gbc1(row++, 1));
        editPanel.add(new JLabel("时间:"), gbc0(row, 0)); editPanel.add(tfTime, gbc1(row++, 1));
        editPanel.add(new JLabel("模式:"), gbc0(row, 0)); editPanel.add(tfMode, gbc1(row++, 1));
        editPanel.add(new JLabel("好感:"), gbc0(row, 0)); editPanel.add(spDiaryAffection, gbc1(row++, 1));
        editPanel.add(new JLabel("内容:"), gbc0(row, 0)); editPanel.add(new JScrollPane(taDiaryContent), gbc1(row++, 1));

        listDiary.addListSelectionListener(e -> {
            int idx = listDiary.getSelectedIndex();
            if (idx >= 0 && saveData != null && idx < saveData.data.diary.size()) {
                var entry = saveData.data.diary.get(idx);
                tfDate.setText(entry.date); tfTime.setText(entry.time); tfMode.setText(entry.mode);
                spDiaryAffection.setValue(entry.affection); taDiaryContent.setText(entry.content);
            }
        });

        var btnPanel = new JPanel();
        var btnSaveDiary = new JButton("保存修改");
        btnSaveDiary.addActionListener(e -> {
            int idx = listDiary.getSelectedIndex();
            if (idx >= 0 && saveData != null && idx < saveData.data.diary.size()) {
                var entry = saveData.data.diary.get(idx);
                entry.date = tfDate.getText(); entry.time = tfTime.getText();
                entry.mode = tfMode.getText(); entry.affection = (Integer) spDiaryAffection.getValue();
                entry.content = taDiaryContent.getText();
                refreshDiaryList();
            }
        });
        btnPanel.add(btnSaveDiary);
        editPanel.add(btnPanel, gbc1(row, 1));
        p.add(editPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildPromptPanel() {
        var p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new TitledBorder("人格提示词"));
        String[] promptKeys = {"sister-null", "sister-verylow", "sister-low", "sister-medium",
                "sister-high", "sister-dilei", "sister-kindergarten", "sister-tutor",
                "sister-kemonomimi", "sister-kemonomimi-cat"};
        cmbPromptSelect = new JComboBox<>(promptKeys);
        p.add(cmbPromptSelect, BorderLayout.NORTH);

        var editPanel = new JPanel(new GridBagLayout());
        var gbc = gbc(); int row = 0;
        taDescription = new JTextArea(3, 30); taPersonality = new JTextArea(2, 30);
        taScenario = new JTextArea(3, 30); taCreatorNotes = new JTextArea(5, 30);
        taFirstMes = new JTextArea(2, 30); taTags = new JTextArea(2, 30);

        editPanel.add(new JLabel("描述:"), gbc0(row, 0)); editPanel.add(new JScrollPane(taDescription), gbc1(row++, 1));
        editPanel.add(new JLabel("性格:"), gbc0(row, 0)); editPanel.add(new JScrollPane(taPersonality), gbc1(row++, 1));
        editPanel.add(new JLabel("场景:"), gbc0(row, 0)); editPanel.add(new JScrollPane(taScenario), gbc1(row++, 1));
        editPanel.add(new JLabel("创作者笔记:"), gbc0(row, 0)); editPanel.add(new JScrollPane(taCreatorNotes), gbc1(row++, 1));
        editPanel.add(new JLabel("开场白:"), gbc0(row, 0)); editPanel.add(new JScrollPane(taFirstMes), gbc1(row++, 1));
        editPanel.add(new JLabel("标签:"), gbc0(row, 0)); editPanel.add(new JScrollPane(taTags), gbc1(row++, 1));

        cmbPromptSelect.addActionListener(e -> loadPromptToUI((String) cmbPromptSelect.getSelectedItem()));
        var btnSavePrompt = new JButton("保存 Prompt");
        btnSavePrompt.addActionListener(e -> savePromptFromUI());
        editPanel.add(btnSavePrompt, gbc1(row, 1));
        p.add(new JScrollPane(editPanel), BorderLayout.CENTER);
        return p;
    }

    // ---- Data loading/saving ----
    private void loadDataToUI() {
        if (saveData == null) return;
        var gd = saveData.data.gameData;
        var pi = gd.playerInfo;
        var cs = gd.characterSystemData;
        var ch = cs.character;

        tfName.setText(pi.name); tfBirthday.setText(pi.birthday); tfIdentity.setText(pi.identity);
        cbCollected.setSelected(pi.hasCollected); tfGender.setText(gd.gender);
        spAffection.setValue(gd.characterStats.affection); spTrust.setValue(gd.characterStats.trust);
        spSystemAffection.setValue(cs.stats.affection); spSystemTrust.setValue(cs.stats.trust);
        tfCharName.setText(ch.name); tfCharPersonality.setText(ch.personality); tfCharMood.setText(ch.currentMood);

        cbDiaryLeak.setSelected(cs.diaryLeakTriggered); cbYandere.setSelected(cs.yandereEventTriggered);
        cbGiftPref.setSelected(cs.giftPreferenceEventTriggered); cbAff30.setSelected(cs.affection30EventTriggered);
        cbAff70.setSelected(cs.affection70EventTriggered);
        spAffectionCap.setValue(cs.affectionCap); spTrustMin.setValue(cs.trustMin); spTrustMax.setValue(cs.trustMax);
        spShyness.setValue(cs.kemonomimiShyness); cbCatForm.setSelected(cs.kemonomimiCatForm);
        tfPersonaPackId.setText(cs.currentPersonaPackId != null ? cs.currentPersonaPackId : "");
        cbDiaryRecovered.setSelected(cs.diaryLeakRecovered); cbYandereRecovered.setSelected(cs.yandereRecovered);
        cbOutfit19.setSelected(cs.outfit19Unlocked);
        tfCurrentOutfit.setText(gd.currentOutfit != null ? gd.currentOutfit : "");
        spReminderCount.setValue(gd.kemonomimiReminderCount);

        giftsModel.clear(); ch.favoriteGifts.forEach(giftsModel::addElement);
        eventsModel.clear(); ch.specialEvents.forEach(eventsModel::addElement);

        spCoins.setValue(gd.playerProgress.coins); spQuestLevel.setValue(gd.questSystem.unlockedLevel);
        spGlobalLevel.setValue(gd.globalLevelSystem.globalLevel); spGlobalExp.setValue(gd.globalLevelSystem.globalExp);
        spCurrentDay.setValue(gd.timeSystem.currentDay); spDailyActions.setValue(gd.timeSystem.dailyActions);
        spActionsUsed.setValue(gd.timeSystem.actionsUsed); tfGameStartDate.setText(gd.timeSystem.gameStartDate);
        timeEventsModel.clear(); gd.timeSystem.specialEvents.forEach(timeEventsModel::addElement);

        var shop = gd.shopSystem;
        for (int i = 0; i < 12; i++) spGifts[i].setValue(getGiftValue(shop.giftInventory, i + 1));
        for (int i = 0; i < 20; i++) cbOutfits[i].setSelected(getOutfitValue(shop.purchases, i + 1));
        tfShopTab.setText(shop.currentTab);

        String[] dateLabels = {"custom", "cafe", "park", "restaurant", "beach", "nightmarket", "nightpath"};
        for (int i = 0; i < 7; i++) { int v = getDateValue(gd.dateHistory, i); spDateHistory[i].setValue(v); }

        var ts = gd.travelSystem;
        cbTravelMode.setSelected(ts.isTravelMode); cbNightDialogue.setSelected(ts.hasShownNightDialogue);
        tfDestName.setText(ts.currentDestination != null ? ts.currentDestination : "");
        tfCityName.setText(ts.destinationCityName != null ? ts.destinationCityName : "");
        tfCurrentCity.setText(ts.currentCity != null ? ts.currentCity : "");
        tfTravelState.setText(ts.travelState != null ? ts.travelState : "");
        tfTravelStart.setText(ts.travelStartTime != null ? ts.travelStartTime : "");
        tfTravelEnd.setText(ts.travelEndTime != null ? ts.travelEndTime : "");

        var gs = gd.settings;
        slMusic.setValue((int) (gs.musicVolume * 100)); slSound.setValue((int) (gs.soundVolume * 100));
        cbAutoSave.setSelected(gs.autoSave);

        var as = saveData.data.settings;
        slBgm.setValue(as.bgmVolume); slVoice.setValue(as.voiceVolume);
        cbMuted.setSelected(as.isMuted); cbTts.setSelected(as.ttsEnabled); cbPreset.setSelected(as.presetRepliesEnabled);

        refreshDiaryList();
        loadPromptToUI((String) cmbPromptSelect.getSelectedItem());
    }

    private void saveDataFromUI() {
        if (saveData == null) return;
        var gd = saveData.data.gameData;
        var pi = gd.playerInfo;
        pi.name = tfName.getText(); pi.birthday = tfBirthday.getText(); pi.identity = tfIdentity.getText();
        pi.hasCollected = cbCollected.isSelected(); gd.gender = tfGender.getText();
        gd.characterStats.affection = (Integer) spAffection.getValue(); gd.characterStats.trust = (Integer) spTrust.getValue();

        var cs = gd.characterSystemData;
        cs.stats.affection = (Integer) spSystemAffection.getValue(); cs.stats.trust = (Integer) spSystemTrust.getValue();
        cs.character.name = tfCharName.getText(); cs.character.personality = tfCharPersonality.getText();
        cs.character.currentMood = tfCharMood.getText();
        cs.character.favoriteGifts = listToList(giftsModel);
        cs.character.specialEvents = listToList(eventsModel);
        cs.diaryLeakTriggered = cbDiaryLeak.isSelected(); cs.yandereEventTriggered = cbYandere.isSelected();
        cs.giftPreferenceEventTriggered = cbGiftPref.isSelected();
        cs.affection30EventTriggered = cbAff30.isSelected(); cs.affection70EventTriggered = cbAff70.isSelected();
        cs.affectionCap = (Integer) spAffectionCap.getValue(); cs.trustMin = (Integer) spTrustMin.getValue();
        cs.trustMax = (Integer) spTrustMax.getValue(); cs.kemonomimiShyness = (Integer) spShyness.getValue();
        cs.kemonomimiCatForm = cbCatForm.isSelected();
        cs.currentPersonaPackId = tfPersonaPackId.getText().isBlank() ? null : tfPersonaPackId.getText();
        cs.diaryLeakRecovered = cbDiaryRecovered.isSelected(); cs.yandereRecovered = cbYandereRecovered.isSelected();
        cs.outfit19Unlocked = cbOutfit19.isSelected();
        gd.currentOutfit = tfCurrentOutfit.getText().isBlank() ? null : tfCurrentOutfit.getText();
        gd.kemonomimiReminderCount = (Integer) spReminderCount.getValue();

        gd.playerProgress.coins = (Integer) spCoins.getValue(); gd.questSystem.unlockedLevel = (Integer) spQuestLevel.getValue();
        gd.globalLevelSystem.globalLevel = (Integer) spGlobalLevel.getValue(); gd.globalLevelSystem.globalExp = (Integer) spGlobalExp.getValue();
        gd.timeSystem.currentDay = (Integer) spCurrentDay.getValue(); gd.timeSystem.dailyActions = (Integer) spDailyActions.getValue();
        gd.timeSystem.actionsUsed = (Integer) spActionsUsed.getValue(); gd.timeSystem.gameStartDate = tfGameStartDate.getText();
        gd.timeSystem.specialEvents = listToList(timeEventsModel);

        for (int i = 0; i < 12; i++) setGiftValue(gd.shopSystem.giftInventory, i + 1, (Integer) spGifts[i].getValue());
        for (int i = 0; i < 20; i++) setOutfitValue(gd.shopSystem.purchases, i + 1, cbOutfits[i].isSelected());
        gd.shopSystem.currentTab = tfShopTab.getText();

        int[] dv = new int[7];
        for (int i = 0; i < 7; i++) dv[i] = (Integer) spDateHistory[i].getValue();
        gd.dateHistory.custom = dv[0]; gd.dateHistory.cafe = dv[1]; gd.dateHistory.park = dv[2];
        gd.dateHistory.restaurant = dv[3]; gd.dateHistory.beach = dv[4];
        gd.dateHistory.nightmarket = dv[5]; gd.dateHistory.nightpath = dv[6];

        gd.settings.musicVolume = slMusic.getValue() / 100.0; gd.settings.soundVolume = slSound.getValue() / 100.0;
        gd.settings.autoSave = cbAutoSave.isSelected();

        var ts = gd.travelSystem;
        ts.isTravelMode = cbTravelMode.isSelected(); ts.hasShownNightDialogue = cbNightDialogue.isSelected();
        ts.currentDestination = n(tfDestName.getText()); ts.destinationCityName = n(tfCityName.getText());
        ts.currentCity = n(tfCurrentCity.getText()); ts.travelState = n(tfTravelState.getText());
        ts.travelStartTime = n(tfTravelStart.getText()); ts.travelEndTime = n(tfTravelEnd.getText());

        var as = saveData.data.settings;
        as.bgmVolume = slBgm.getValue(); as.voiceVolume = slVoice.getValue();
        as.isMuted = cbMuted.isSelected(); as.ttsEnabled = cbTts.isSelected(); as.presetRepliesEnabled = cbPreset.isSelected();

        savePromptFromUI();
    }

    // -- Prompt helpers --
    private void loadPromptToUI(String key) {
        if (saveData == null || key == null) return;
        PromptsMap.PromptData pd = getPromptByKey(key);
        taDescription.setText(pd.data.description); taPersonality.setText(pd.data.personality);
        taScenario.setText(pd.data.scenario); taCreatorNotes.setText(pd.data.creatorNotes);
        taFirstMes.setText(pd.data.firstMes != null ? pd.data.firstMes : "");
        taTags.setText(String.join(", ", pd.data.tags));
    }

    private void savePromptFromUI() {
        String key = (String) cmbPromptSelect.getSelectedItem();
        if (saveData == null || key == null) return;
        PromptsMap.PromptData pd = getPromptByKey(key);
        pd.data.description = taDescription.getText(); pd.data.personality = taPersonality.getText();
        pd.data.scenario = taScenario.getText(); pd.data.creatorNotes = taCreatorNotes.getText();
        pd.data.firstMes = taFirstMes.getText().isBlank() ? null : taFirstMes.getText();
        pd.data.tags = java.util.Arrays.asList(taTags.getText().split(",\\s*"));
    }

    private PromptsMap.PromptData getPromptByKey(String key) {
        PromptsMap.PromptData pd = switch (key) {
            case "sister-null" -> saveData.data.prompts.sisterNull;
            case "sister-verylow" -> saveData.data.prompts.sisterVerylow;
            case "sister-low" -> saveData.data.prompts.sisterLow;
            case "sister-medium" -> saveData.data.prompts.sisterMedium;
            case "sister-high" -> saveData.data.prompts.sisterHigh;
            case "sister-dilei" -> saveData.data.prompts.sisterDilei;
            case "sister-kindergarten" -> saveData.data.prompts.sisterKindergarten;
            case "sister-tutor" -> saveData.data.prompts.sisterTutor;
            case "sister-kemonomimi" -> saveData.data.prompts.sisterKemonomimi;
            case "sister-kemonomimi-cat" -> saveData.data.prompts.sisterKemonomimiCat;
            default -> null;
        };
        if (pd == null) {
            pd = new PromptsMap.PromptData();
            setPromptByKey(key, pd);
        }
        return pd;
    }

    private void setPromptByKey(String key, PromptsMap.PromptData pd) {
        switch (key) {
            case "sister-null" -> saveData.data.prompts.sisterNull = pd;
            case "sister-verylow" -> saveData.data.prompts.sisterVerylow = pd;
            case "sister-low" -> saveData.data.prompts.sisterLow = pd;
            case "sister-medium" -> saveData.data.prompts.sisterMedium = pd;
            case "sister-high" -> saveData.data.prompts.sisterHigh = pd;
            case "sister-dilei" -> saveData.data.prompts.sisterDilei = pd;
            case "sister-kindergarten" -> saveData.data.prompts.sisterKindergarten = pd;
            case "sister-tutor" -> saveData.data.prompts.sisterTutor = pd;
            case "sister-kemonomimi" -> saveData.data.prompts.sisterKemonomimi = pd;
            case "sister-kemonomimi-cat" -> saveData.data.prompts.sisterKemonomimiCat = pd;
        }
    }

    private void refreshDiaryList() {
        diaryModel.clear();
        if (saveData != null) saveData.data.diary.forEach(e -> diaryModel.addElement(e.date + " " + e.time + " [" + e.mode + "]"));
    }

    // ---- Swing helpers ----
    private GridBagConstraints gbc() { var c = new GridBagConstraints(); c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(2, 4, 2, 4); return c; }
    private GridBagConstraints gbc0(int y, int x) { var c = gbc(); c.gridy = y; c.gridx = x; c.weightx = 0; c.anchor = GridBagConstraints.EAST; return c; }
    private GridBagConstraints gbc1(int y, int x) { var c = gbc(); c.gridy = y; c.gridx = x; c.weightx = 1; c.gridwidth = 1; return c; }

    private JTextField addField(JPanel p, String label, String def, GridBagConstraints gbc, int row) {
        p.add(new JLabel(label + ":"), gbc0(row, 0)); var tf = new JTextField(def, 15); p.add(tf, gbc1(row, 1)); return tf;
    }
    private JSpinner addSpinner(JPanel p, String label, int min, int max, GridBagConstraints gbc, int row) {
        p.add(new JLabel(label + ":"), gbc0(row, 0)); var sp = new JSpinner(new SpinnerNumberModel(min, min, max, 1)); p.add(sp, gbc1(row, 1)); return sp;
    }
    private JSlider addSlider(JPanel p, String label, int min, int max, int def, GridBagConstraints gbc, int row) {
        p.add(new JLabel(label + ":"), gbc0(row, 0)); var sl = new JSlider(min, max, def); sl.setMajorTickSpacing(25); sl.setPaintTicks(true); sl.setPaintLabels(true); p.add(sl, gbc1(row, 1)); return sl;
    }
    private JCheckBox addCheck(JPanel p, String label, GridBagConstraints gbc, int row) {
        p.add(new JLabel(""), gbc0(row, 0)); var cb = new JCheckBox(label); p.add(cb, gbc1(row, 1)); return cb;
    }

    private String n(String s) { return s == null || s.isBlank() ? null : s; }
    private java.util.List<String> listToList(DefaultListModel<String> m) { return java.util.Collections.list(m.elements()); }

    // Reflection-based outfit and gift accessors
    private boolean getOutfitValue(GameData.OutfitPurchases p, int i) {
        return switch (i) {
            case 1 -> p.outfit1; case 2 -> p.outfit2; case 3 -> p.outfit3; case 4 -> p.outfit4;
            case 5 -> p.outfit5; case 6 -> p.outfit6; case 7 -> p.outfit7; case 8 -> p.outfit8;
            case 9 -> p.outfit9; case 10 -> p.outfit10; case 11 -> p.outfit11; case 12 -> p.outfit12;
            case 13 -> p.outfit13; case 14 -> p.outfit14; case 15 -> p.outfit15; case 16 -> p.outfit16;
            case 17 -> p.outfit17; case 18 -> p.outfit18; case 19 -> p.outfit19; case 20 -> p.outfit20;
            default -> false;
        };
    }
    private void setOutfitValue(GameData.OutfitPurchases p, int i, boolean v) {
        switch (i) {
            case 1 -> p.outfit1 = v; case 2 -> p.outfit2 = v; case 3 -> p.outfit3 = v; case 4 -> p.outfit4 = v;
            case 5 -> p.outfit5 = v; case 6 -> p.outfit6 = v; case 7 -> p.outfit7 = v; case 8 -> p.outfit8 = v;
            case 9 -> p.outfit9 = v; case 10 -> p.outfit10 = v; case 11 -> p.outfit11 = v; case 12 -> p.outfit12 = v;
            case 13 -> p.outfit13 = v; case 14 -> p.outfit14 = v; case 15 -> p.outfit15 = v; case 16 -> p.outfit16 = v;
            case 17 -> p.outfit17 = v; case 18 -> p.outfit18 = v; case 19 -> p.outfit19 = v; case 20 -> p.outfit20 = v;
        }
    }
    private int getGiftValue(GameData.GiftInventory g, int i) {
        return switch (i) {
            case 1 -> g.gift1; case 2 -> g.gift2; case 3 -> g.gift3; case 4 -> g.gift4;
            case 5 -> g.gift5; case 6 -> g.gift6; case 7 -> g.gift7; case 8 -> g.gift8;
            case 9 -> g.gift9; case 10 -> g.gift10; case 11 -> g.gift11; case 12 -> g.gift12;
            default -> 0;
        };
    }
    private void setGiftValue(GameData.GiftInventory g, int i, int v) {
        switch (i) {
            case 1 -> g.gift1 = v; case 2 -> g.gift2 = v; case 3 -> g.gift3 = v; case 4 -> g.gift4 = v;
            case 5 -> g.gift5 = v; case 6 -> g.gift6 = v; case 7 -> g.gift7 = v; case 8 -> g.gift8 = v;
            case 9 -> g.gift9 = v; case 10 -> g.gift10 = v; case 11 -> g.gift11 = v; case 12 -> g.gift12 = v;
        }
    }
    private int getDateValue(GameData.DateHistory dh, int i) {
        return switch (i) {
            case 0 -> dh.custom; case 1 -> dh.cafe; case 2 -> dh.park; case 3 -> dh.restaurant;
            case 4 -> dh.beach; case 5 -> dh.nightmarket; case 6 -> dh.nightpath;
            default -> 0;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            var frame = new Main();
            frame.setVisible(true);
            // Auto-load template on startup
            frame.loadTemplate();
        });
    }
}
