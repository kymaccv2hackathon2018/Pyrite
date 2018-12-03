load("@io_bazel_rules_go//go:def.bzl", "go_binary", "go_library")
load("@io_bazel_rules_go//proto:def.bzl", "go_proto_library")

go_library(
    name = "go_default_library",
    srcs = [
        "main.go",
        "serve.go",
    ],
    importpath = "github.wdf.sap.corp/team-pyrite/hackathon/eventgen",
    visibility = ["//visibility:private"],
    deps = [
        ":commerce_go_proto_library",
        "@com_github_golang_protobuf//jsonpb:go_default_library_gen",
        "@com_github_sirupsen_logrus//:go_default_library",
        "@com_github_urfave_cli//:go_default_library",
    ],
)

go_binary(
    name = "eventgen",
    embed = [":go_default_library"],
    visibility = ["//visibility:public"],
)

go_proto_library(
    name = "commerce_go_proto_library",
    importpath = "github.wdf.sap.corp/team-pyrite/hackathon/proto/commerce",
    proto = "//eventgen/proto/commerce:commerce_proto",
    visibility = ["//visibility:public"],
)
