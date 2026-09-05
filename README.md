# netbeans-intelephense-lsp

A minimal Apache NetBeans module that registers [Intelephense](https://intelephense.com/) (the PHP Language Server Protocol implementation) with NetBeans' built-in generic LSP client (`org-netbeans-modules-lsp-client`), so Intelephense can be used as an additional PHP language server inside NetBeans.

It runs alongside NetBeans' own native PHP editor support rather than replacing it — NetBeans' PHP module does not route through the generic LSP client mechanism, so both may offer completions/hints at the same time.

## How the two PHP providers cooperate

Confirmed by reading the Apache NetBeans source (`ide/editor.completion`, `ide/lsp.client`, `platform/editor.mimelookup*`, `php/php.editor`):

- **Code completion**: NetBeans' `CompletionImpl` queries *every* `CompletionProvider` registered for the mimetype and merges their results into one popup (not first-match-wins). The LSP client's `CompletionProviderImpl` is a global (`mimeType=""`), otherwise-inert provider — it only produces results once a `LanguageServerProvider` (this module) is actually registered/running for the file's mimetype. So installing this plugin *adds* Intelephense's suggestions into the same list as native PHP completions rather than replacing them.
- **Hover/tooltips**: same mechanism as completion (same class handles both) — combined, not competing.
- **Hints/diagnostics**: **not** combined. PHP's native errors (`PHPHintsProvider`) and LSP diagnostics (`LanguageClientImpl`) are two independent, unrelated pipelines — expect to potentially see duplicate error/warning markers from both sources for the same issue.

## Requirements

- Apache NetBeans 21+ (needs the bundled `org-netbeans-modules-lsp-client` module, spec version 1.30+)
- [Intelephense](https://intelephense.com/) installed and on `PATH`:
  ```
  npm i intelephense -g
  ```
- (Optional) an Intelephense Premium licence key placed at `~/intelephense/licence.txt` — Intelephense picks it up automatically without any extra configuration.

## Building

```
ant -Dnbplatform.default.netbeans.dest.dir=/path/to/netbeans \
    -Dnbplatform.default.harness.dir=/path/to/netbeans/harness \
    nbms
```

The resulting plugin is written to `build/updates/com-vitexsoftware-intelephenselsp.nbm`.

## Installing

In NetBeans: **Tools → Plugins → Downloaded → Add Plugins...**, select the `.nbm` file, then **Install** and restart NetBeans.

## How it works

The module implements `org.netbeans.modules.lsp.client.spi.LanguageServerProvider`, registered via `@MimeRegistration` for `text/x-php5` — this is NetBeans' actual internal mimetype for PHP files (see `php-project-mime-resolver.xml`; despite the "x-php5" name it applies to all `.php`/`.phtml` files regardless of PHP version). On activation it launches `intelephense --stdio` and hands the process's stdio streams to NetBeans' LSP client.
