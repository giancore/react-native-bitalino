require 'json'

package = JSON.parse(File.read(File.join(__dir__, '..', 'package.json')))

Pod::Spec.new do |s|
  s.name           = 'ReactNativeBitalino'
  s.version        = package['version']
  s.summary        = package['description']
  s.description    = package['description']
  s.license        = package['license']
  s.author         = package['author']
  s.homepage       = package['homepage']
  s.platforms      = { :ios => '13.4', :tvos => '13.4' }
  s.swift_version  = '5.4'
  s.source         = { git: 'https://github.com/giancore/react-native-bitalino' }
  s.static_framework = true

  s.dependency 'ExpoModulesCore'

  # Swift/Objective-C compatibility
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES',
    'SWIFT_COMPILATION_MODE' => 'wholemodule'
  }

  s.source_files = "**/*.{h,m,swift}"
  
  # Automatically include PluxAPI.framework if present
  # Download framework from: https://www.pluxbiosignals.com/pages/download-software
  # Place at: ios/Frameworks/PluxAPI.framework
  # See ios/Frameworks/README.md for detailed instructions
  s.vendored_frameworks = "Frameworks/PluxAPI.framework"
  
  # Framework search paths
  s.xcconfig = {
    'FRAMEWORK_SEARCH_PATHS' => '"$(PODS_ROOT)/../Frameworks" "$(PODS_TARGET_SRCROOT)/Frameworks"'
  }
end
