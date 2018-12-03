load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# ===============================================================
# rules_proto
# ===============================================================

http_archive(
    name = "build_stack_rules_proto",
    urls = ["https://github.com/stackb/rules_proto/archive/4c2226458203a9653ae722245cc27e8b07c383f7.tar.gz"],
    sha256 = "0be90d609fcefae9cc5e404540b9b23176fb609c9d62f4f9f68528f66a6839bf",
    strip_prefix = "rules_proto-4c2226458203a9653ae722245cc27e8b07c383f7",
)

# ===============================================================
# rules_go
# ===============================================================

load("@build_stack_rules_proto//go:deps.bzl", "go_grpc_library")

go_grpc_library()

load("@io_bazel_rules_go//go:def.bzl", "go_rules_dependencies", "go_register_toolchains")

go_rules_dependencies()

go_register_toolchains()

# ===============================================================
# java protobuf / grpc
# ===============================================================

load("@build_stack_rules_proto//:deps.bzl", "io_grpc_grpc_java")

io_grpc_grpc_java()

load("@io_grpc_grpc_java//:repositories.bzl", "grpc_java_repositories")

grpc_java_repositories(omit_com_google_protobuf = True)

load("@build_stack_rules_proto//java:deps.bzl", "java_grpc_library")

java_grpc_library()

load("@build_stack_rules_proto//java:deps.bzl", "java_proto_compile")

java_proto_compile()

# ===============================================================
# node
# ===============================================================

load("@build_stack_rules_proto//node:deps.bzl", "node_proto_library")

node_proto_library()

load("@org_pubref_rules_node//node:rules.bzl", "node_repositories", "yarn_modules")

node_repositories()

yarn_modules(
    name = "proto_node_modules",
    deps = {
        "google-protobuf": "3.6.1",
    },
)

# ===============================================================
# bazel_gazelle
# ===============================================================

load("@build_stack_rules_proto//:deps.bzl", "bazel_gazelle")

bazel_gazelle()

load("@bazel_gazelle//:deps.bzl", "gazelle_dependencies", "go_repository")

gazelle_dependencies()

# gazelle:repo bazel_gazelle

# ===============================================================
# starlark-go
# ===============================================================

go_repository(
    name = "com_github_sirupsen_logrus",
    commit = "29d7eb25e8ffa54207ff5a9a5c3d63e95be2cc39",
    importpath = "github.com/sirupsen/logrus",
)

go_repository(
    name = "com_github_urfave_cli",
    commit = "b67dcf995b6a7b7f14fad5fcb7cc5441b05e814b",
    importpath = "github.com/urfave/cli",
)

go_repository(
    name = "org_golang_x_crypto",
    importpath = "golang.org/x/crypto",
    urls = ["https://github.com/golang/crypto/archive/12892e8c234f4fe6f6803f052061de9057903bb2.tar.gz"],
    strip_prefix = "crypto-12892e8c234f4fe6f6803f052061de9057903bb2/",
    build_file_generation = "on",
    build_file_proto_mode = "disable",
)
