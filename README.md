# SIM切替

デュアルSIM端末向けのSIM情報表示 & 設定ショートカットアプリ

## 機能

- SIMカード情報の一覧表示（キャリア名・スロット番号）
- デフォルトSIMの表示（通話・データ・SMS）
- 各SIMの詳細設定画面へワンタップジャンプ
- SIM一覧設定画面へのショートカット
- Quick Settingsタイル対応（通知バーからワンタップでSIM設定へ）

## インストール

[Releases](../../releases) からAPKをダウンロードしてインストールしてください。

## 必要な権限

- `READ_PHONE_STATE` - SIMカード情報の表示に使用

## プライバシーポリシー

- 広告なし
- データ送信なし
- 取得した情報は画面表示のみに使用し、外部に送信しません
- 詳細は [プライバシーポリシー](store/privacy-policy.html) をご覧ください

## ビルド

```
cd SimSwitcher
./gradlew assembleDebug
```

## 動作確認済み端末

- Pixel 9a (Android 16)
- Xperia XQ-CQ44 (Android 14)
