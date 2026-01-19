# GitHub Actions Workflows

Este projeto usa GitHub Actions para CI/CD automatizado. Aqui está uma visão geral dos workflows configurados:

## 📋 Workflows Disponíveis

### 1. **CI Pipeline** ([`.github/workflows/ci.yml`](.github/workflows/ci.yml))

Executa em todos os PRs e pushes para `master`/`develop`.

**Jobs:**

- **Lint & Type Check**: ESLint + TypeScript validation
- **Build Module**: Compila o módulo e valida outputs
- **Validate Android**: Gradle build e sintaxe Kotlin
- **Validate iOS**: Podspec lint e sintaxe Swift
- **Validate Example**: Type check do app de exemplo
- **Security Audit**: npm audit para vulnerabilidades

**Triggers:**

```yaml
on:
  pull_request:
    branches: [master, develop]
  push:
    branches: [master, develop]
```

### 2. **Release** ([`.github/workflows/release.yml`](.github/workflows/release.yml))

Publica automaticamente no NPM quando há mudanças em `master`.

**Jobs:**

- **Pre-Release Validation**: Lint, type check, build
- **Release to NPM**: Usa Changesets para versionamento semântico
- **GitHub Release**: Cria release notes automaticamente

**Features:**

- ✅ Versionamento automático com Changesets
- ✅ Changelog gerado automaticamente
- ✅ GitHub Release com tags
- ✅ Validação antes de publicar

**Triggers:**

```yaml
on:
  push:
    branches: [master]
    paths-ignore: ["**.md", "docs/**"]
```

### 3. **PR Validation** ([`.github/workflows/pr-validation.yml`](.github/workflows/pr-validation.yml))

Validações específicas para Pull Requests.

**Jobs:**

- **PR Quality Check**: Valida título do PR (Conventional Commits)
- **Size Check**: Monitora tamanho do build
- **Dependency Review**: Analisa dependências novas/modificadas

**PR Title Format:**

```
feat: add new feature
fix: resolve bug
docs: update documentation
chore: update dependencies
```

## 🔑 Secrets Necessários

Configure os seguintes secrets no GitHub (`Settings > Secrets and variables > Actions`):

| Secret         | Descrição              | Obtenção                                                           |
| -------------- | ---------------------- | ------------------------------------------------------------------ |
| `NPM_TOKEN`    | Token de acesso do NPM | [npmjs.com/settings/tokens](https://www.npmjs.com/settings/tokens) |
| `GITHUB_TOKEN` | Gerado automaticamente | Não precisa configurar                                             |

### Como criar NPM_TOKEN:

1. Acesse [npmjs.com/settings/tokens](https://www.npmjs.com/settings/tokens)
2. Clique em "Generate New Token"
3. Selecione "Automation" (para CI/CD)
4. Copie o token e adicione como secret no GitHub

## 🚀 Fluxo de Release

### Usando Changesets

1. **Faça suas mudanças**

   ```bash
   # Edite código, adicione features, corrija bugs
   ```

2. **Crie um changeset**

   ```bash
   npm run changeset
   ```

   - Selecione o tipo de mudança: `major`, `minor`, ou `patch`
   - Descreva as mudanças

3. **Commit e push**

   ```bash
   git add .changeset/
   git commit -m "feat: add new feature"
   git push
   ```

4. **PR automático de versão**

   - GitHub Actions cria um PR com "chore: version packages"
   - Revise o CHANGELOG.md gerado

5. **Merge para publicar**
   - Merge do PR → publica automaticamente no NPM
   - Cria GitHub Release com tag

## 📊 Status Badges

Adicione ao README.md:

```markdown
[![CI](https://github.com/giancore/react-native-bitalino/workflows/CI/badge.svg)](https://github.com/giancore/react-native-bitalino/actions/workflows/ci.yml)
[![Release](https://github.com/giancore/react-native-bitalino/workflows/Release/badge.svg)](https://github.com/giancore/react-native-bitalino/actions/workflows/release.yml)
```

## 🔧 Troubleshooting

### Build falha no CI mas funciona localmente

```bash
# Limpe e reconstrua
npm run clean
npm ci
npm run build
```

### Workflow de release não publica

1. Verifique se `NPM_TOKEN` está configurado
2. Confirme que há changesets em `.changeset/`
3. Verifique logs no Actions tab

### Validate iOS falha

- Normal se PluxAPI.framework não estiver commitado
- O workflow tenta validar mas pula se framework não existir
- Use `--allow-warnings` no `pod lib lint`

## 📝 Conventional Commits

O projeto usa Conventional Commits para PRs:

| Tipo        | Uso                                      |
| ----------- | ---------------------------------------- |
| `feat:`     | Nova funcionalidade                      |
| `fix:`      | Correção de bug                          |
| `docs:`     | Mudanças em documentação                 |
| `style:`    | Formatação, sem mudança de código        |
| `refactor:` | Refatoração sem mudança de comportamento |
| `perf:`     | Melhoria de performance                  |
| `test:`     | Adicionar/corrigir testes                |
| `build:`    | Build system, dependências               |
| `ci:`       | Mudanças em CI/CD                        |
| `chore:`    | Outras mudanças que não afetam src       |

### Exemplos:

```bash
git commit -m "feat: add PWM control to iOS"
git commit -m "fix: resolve connection timeout on Android"
git commit -m "docs: update API reference"
git commit -m "chore: upgrade dependencies"
```

## 🎯 Próximos Passos

- [ ] Configurar `NPM_TOKEN` secret
- [ ] Criar primeiro changeset
- [ ] Testar workflow de release
- [ ] Adicionar badges ao README
- [ ] Configurar branch protection rules

## 🔗 Links Úteis

- [Changesets Documentation](https://github.com/changesets/changesets)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
