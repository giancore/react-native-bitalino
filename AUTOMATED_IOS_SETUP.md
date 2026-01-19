# Automated iOS Setup with CocoaPods

## Summary

✅ **CocoaPods agora gerencia automaticamente o PluxAPI.framework!**

Você só precisa:

1. Baixar o framework uma vez
2. Colocar na pasta correta
3. Rodar `pod install`

## Configuração Realizada

### 1. Estrutura de Diretórios

```
ios/
└── Frameworks/
    ├── README.md (instruções detalhadas)
    └── PluxAPI.framework/ (você baixa e coloca aqui)
```

### 2. Podspec Atualizado

```ruby
s.vendored_frameworks = "Frameworks/PluxAPI.framework"
s.xcconfig = {
  'FRAMEWORK_SEARCH_PATHS' => '"$(PODS_ROOT)/../Frameworks" "$(PODS_TARGET_SRCROOT)/Frameworks"'
}
```

Isso faz o CocoaPods:

- ✅ Detectar automaticamente o framework
- ✅ Configurar os search paths
- ✅ Linkar e embedar o framework
- ✅ Configurar assinatura

### 3. .gitignore Atualizado

```
ios/Frameworks/PluxAPI.framework/
```

O framework não é commitado no git (evita aumentar repositório em ~20MB).

## Processo de Setup para Usuários

### Passo 1: Baixar Framework (uma vez)

```bash
# Baixar de: https://www.pluxbiosignals.com/pages/download-software
# Ou: https://www.pluxbiosignals.com/downloads/PLUX%20iOS%20API%20v1.0.2.zip
```

### Passo 2: Colocar no Local Correto

```bash
# Extrair e copiar para:
react-native-bitalino/ios/Frameworks/PluxAPI.framework/

# Estrutura deve ser:
ios/Frameworks/PluxAPI.framework/
├── Headers/
│   ├── PluxAPI.h
│   └── PluxAPI-Swift.h
├── Modules/
│   └── PluxAPI.swiftmodule/
├── PluxAPI (binary)
└── Info.plist
```

### Passo 3: Instalar Pods

```bash
cd ios
pod install
cd ..
```

### Passo 4: Descomentar Código

```bash
# Editar: ios/ReactNativeBitalinoModule.swift
# Linha 17: // import PluxAPI → import PluxAPI
# Linha 19: /* , PXBioPluxManagerDelegate */ → , PXBioPluxManagerDelegate
# Linhas 30-32: Descomentar properties
# Linhas 280+: Descomentar delegate methods
```

### Passo 5: Build

```bash
npx expo run:ios
```

## Benefícios da Nova Abordagem

### Antes (Manual)

❌ 10+ passos manuais no Xcode
❌ Configuração repetitiva por projeto
❌ Propenso a erros
❌ Difícil troubleshooting

### Agora (Automatizado)

✅ Apenas 5 passos simples
✅ CocoaPods gerencia tudo
✅ Funciona consistentemente
✅ Fácil debug (outputs do CocoaPods)

## Por Que Não Commitamos o Framework?

### Opção A: Commit no Git (❌ Não recomendado)

- **Prós**: Setup ainda mais fácil (sem download)
- **Contras**:
  - +20MB no repositório
  - Problemas de licença (framework não é nosso)
  - Dificulta updates do framework
  - Aumenta clone time

### Opção B: Download Manual + CocoaPods (✅ Escolhida)

- **Prós**:
  - Repositório leve
  - Respeita licença da PLUX
  - Usuário controla versão do framework
  - Processo automatizado após download
- **Contras**:
  - Requer um download manual

## Alternativas Consideradas

### 1. CocoaPods Dependency Remoto

```ruby
s.dependency 'PluxAPI', '~> 1.0.2'
```

❌ PluxAPI não tem podspec público

### 2. Swift Package Manager (SPM)

```swift
.package(url: "https://...", from: "1.0.2")
```

❌ PluxAPI não é distribuído via SPM

### 3. XCFramework com Binary Target

❌ Requer Swift Package, mesmos problemas

### 4. Carthage

❌ Expo Modules usa CocoaPods, não Carthage

## Troubleshooting Comum

### Framework not found

```bash
# Verificar localização:
ls -la ios/Frameworks/PluxAPI.framework/

# Rodar pod install novamente:
cd ios && pod install && cd ..
```

### Symbol not found

```bash
# Limpar build:
cd ios
rm -rf Pods/ Podfile.lock
pod install
cd ..
```

### Architecture mismatch

- Framework deve suportar arm64 (device) e x86_64/arm64 (simulator)
- PluxAPI v1.0.2+ já suporta

## Documentação Atualizada

- ✅ `ios/Frameworks/README.md` - Instruções detalhadas
- ✅ `IOS_SETUP.md` - Setup guide atualizado
- ✅ `README.md` - Status iOS atualizado
- ✅ `ios/ReactNativeBitalino.podspec` - Configurado para vendored_frameworks
- ✅ `.gitignore` - Framework excluído

## Próximos Passos

Para testar:

1. Baixar PluxAPI.framework
2. Colocar em `ios/Frameworks/`
3. Descomentar código Swift
4. `cd example && npx expo run:ios`

Isso deve "just work" ™️
