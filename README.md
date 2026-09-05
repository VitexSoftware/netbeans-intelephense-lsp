# netbeans-intelephense-lsp

A minimal Apache NetBeans module that registers [Intelephense](https://intelephense.com/) (the PHP Language Server Protocol implementation) with NetBeans' built-in generic LSP client (`org-netbeans-modules-lsp-client`), so Intelephense can be used as an additional PHP language server inside NetBeans.

It runs alongside NetBeans' own native PHP editor support rather than replacing it — NetBeans' PHP module does not route through the generic LSP client mechanism, so both may offer completions/hints at the same time.

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

The module implements `org.netbeans.modules.lsp.client.spi.LanguageServerProvider`, registered via `@MimeRegistration` for `text/x-php`. On activation it launches `intelephense --stdio` and hands the process's stdio streams to NetBeans' LSP client.
