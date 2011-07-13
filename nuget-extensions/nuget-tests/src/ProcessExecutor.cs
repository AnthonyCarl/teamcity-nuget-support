using System;
using System.Diagnostics;
using System.IO;
using System.Text;
using System.Threading;

namespace JetBrains.TeamCity.NuGet.Tests
{
  public static class ProcessExecutor
  {
    public static Result ExecuteProcess(string exe, params string[] args)
    {
      var pi = new ProcessStartInfo
                 {
                   FileName = exe,
                   Arguments = string.Join(" ", args),
                   RedirectStandardError = true,
                   RedirectStandardOutput = true,
                   RedirectStandardInput = true,
                   UseShellExecute = false
                 };

      var process = Process.Start(pi);
      process.StandardInput.Close();
      Func<StreamReader, string> readOutput = stream =>
                                                {
                                                  var result = "";
                                                  var th = new Thread(delegate()
                                                                        {
                                                                          var sb = new StringBuilder();
                                                                          int i;
                                                                          while ((i = stream.Read()) >= 0)
                                                                            sb.Append((char) i);
                                                                          result = sb.ToString();
                                                                        })
                                                             {Name = "Process output reader " + process.Id};
                                                  th.Start();
                                                  th.Join(TimeSpan.FromMinutes(10));
                                                  return result;
                                                };

      string output = readOutput(process.StandardOutput);
      string error = readOutput(process.StandardError);
      process.WaitForExit();

      return new Result(output, error, process.ExitCode);
    }

    public class Result
    {
      public string Output { get; private set; }
      public string Error { get; private set; }
      public int ExitCode { get; private set; }

      public Result(string output, string error, int exitCode)
      {
        Output = output;
        Error = error;
        ExitCode = exitCode;
      }

      public override string ToString()
      {
        return new StringBuilder()
          .AppendFormat("ExitCode: {0}\r\n", ExitCode)
          .Append("Output:\n")
          .Append(Output)
          .Append("\n")
          .Append("Error:\n")
          .Append(Error)
          .ToString();
      }
    }
  }
}