# Y700 Launcher Plus — 未完成功能與驗收清單

更新日期：2026-11-07

Lawnchair 15 預覽版最新狀態：Issue #3 抽屜改善已實作並完成主要 Y700 真機驗證；Issue #2 已提供原生編輯入口，直接編輯／同步 ZUI 跨 App Taskbar 仍受系統權限限制。詳見 [實作與驗證紀錄](ISSUES-2-3.md)。以下舊版功能清單與 Lawnchair 遷移狀態需分開判讀。

> 本文件依 README.md、docs/NOVA-PARITY.md、docs/ARCHITECTURE.md、docs/PHASE0.md 與 GOOGLE_PLAY.md 整理。未在文件中確認完成的功能列為待辦或待驗證，不代表程式碼一定不存在。專案既有文件位於 `docs/`，本檔依要求放在 `doc/`。

## 已有基礎（不重複列為待開發）

- [x] 自訂 Android HOME、獨立桌面與可搜尋 App Drawer
- [x] 桌面多分頁、左右滑動、長按加入／移除／跨頁移動 App
- [x] 可設定桌面欄數、顯示／隱藏標籤、隱藏／還原 App
- [x] 自訂 Dock 捷徑跨頁共用
- [x] 版面匯出／匯入與覆蓋確認
- [x] 繁體中文與英文基本介面
- [x] TB320FC / Android 15 / ZUI 17.0.350 基本 HOME 與 ZUI 原生工具列共存畫面驗證
- [x] GitHub Actions APK 建置及固定簽章配置（仍需正式發行驗證）

## P0 — 核心功能與 Y700 相容性

- [ ] 桌面圖示持久化格點座標、順序、自由拖曳與放置
- [ ] 次格點（subgrid）排列與圖示尺寸／間距調整
- [ ] 桌面資料夾：建立、編輯、移動、開啟
- [ ] Android Widget：選擇、放置、縮放、更新與移除
- [ ] App pinned shortcuts／長按快捷操作
- [x] 桌面頁面完整管理：新增、刪除（確認後移除該頁捷徑）、排序與預設頁；待實機驗收
- [x] 啟動器 Dock App 選擇、搜尋、排序與移除（上限 8 個）
- [ ] 啟動器 Dock 多頁與更完整的 Dock 設定
- [ ] 明確區分自訂 Dock 與 ZUI 原生工具列，避免底部點擊區域衝突
- [x] 首次啟動直接開啟自訂 Dock App 設定，保留既有使用者設定；介面說明與 ZUI 原生工具列的區別
- [ ] 確認系統公開 API 是否允許修改 ZUI 原生工具列（目前僅提供自訂 Dock，不呼叫未公開 API）
- [ ] 實機驗證：橫向／直向、其他 App 前景、返回 HOME
- [ ] 實機驗證：分割畫面、浮動視窗、重開機後 HOME 與工具列狀態
- [ ] 實機驗證：Dock 增刪、隱藏／還原 App、升級後資料保留
- [ ] 記錄驗收 APK commit、ZUI build、截圖及 ADB 診斷結果

## P1 — Nova Launcher 功能對齊

### App Drawer 與搜尋
- [ ] Drawer 分頁與資料夾
- [x] Drawer 獨立欄數設定（自動／4–8 欄）
- [ ] Drawer 其他版面配置
- [ ] 垂直／水平瀏覽模式、卡片自訂
- [ ] 搜尋 App shortcuts、聯絡人、計算及單位換算（依權限與可用 API）
- [ ] 第三方搜尋服務接入

### 外觀
- [ ] Icon Pack 支援
- [ ] 單一 App 圖示及名稱編輯
- [ ] Adaptive icon 形狀
- [ ] 圖示尺寸、格線間距、標籤字體與顏色
- [ ] 自訂主題配色、明亮／深色／排程切換
- [ ] 桌布設定、桌面捲動特效

### 手勢與互動
- [ ] 單指／多指手勢設定
- [ ] 雙擊、捏合、滑動等動作
- [ ] 個別圖示上滑／下滑動作
- [ ] 通知圓點及徽章（需評估通知存取授權）
- [ ] 系統操作快捷功能（限公開 API 與使用者授權）

### 備份還原
- [ ] 將資料夾、Widget、捷徑、座標與外觀納入版本化備份格式
- [ ] Widget 重新綁定及還原失敗處理
- [ ] 跨裝置、跨版本匯入測試

## P1 — 發行與維運

- [ ] 正式簽章 APK／AAB 驗證：與永久 Keystore 一致、可覆蓋安裝
- [ ] 永久上傳金鑰與密碼的離線備份驗證
- [ ] Google Play App Signing 與付費 App 設定
- [ ] 隱私權政策、Data safety、法律資訊
- [ ] Google Play 上架素材、商店說明與裝置相容性
- [ ] 上架時確認 targetSdk、權限及 Play 政策
- [ ] 正式版 AAB 手動工作流程與 GitHub Release 驗證
- [ ] 多款 Y700／ZUI 版本相容性回歸測試

## P2 — 可選整合

- [ ] 評估 Google Discover 整合的可行性與限制
- [ ] 其他第三方服務整合，逐項確認 API、授權與行為
- [ ] 補充效能、記憶體、耗電及無障礙測試

## 驗收原則

1. 以同一個 APK、同一個 applicationId（`info.pulipuli.y700launcherplus`）整合功能，不拆成多個驗證 App。
2. 不依賴 Root、系統簽章權限或未公開的 ZUI API。
3. ZUI 原生工具列屬系統控制；不得將自訂 Dock 功能誤寫成能修改原生工具列。
4. 完成功能後須同步勾選本清單、更新 CHANGELOG 與相關文件。
5. APK 由 GitHub Actions 建置，發行資產放 GitHub Releases；CT 僅用於裝置控制與安全簽章管理。
